import java.io.IOException;
import java.util.StringTokenizer;
import java.lang.Float;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Milano {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, FloatWritable> {
        private static FloatWritable max;
        private Text word = new Text();
        private String tokens = "[,]";

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll(tokens, " "));
            while (itr.hasMoreTokens()) {
                String subkey = itr.nextToken();
                word.set(subkey.substring(2).trim());
                itr.nextToken();
                String maxvalue = itr.nextToken().trim();
                max = new FloatWritable(Float.parseFloat(maxvalue));
                context.write(word, max);
            }
        }
    }

    public static class FloatSumReducer extends Reducer<Text, FloatWritable, Text, FloatWritable> {
        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context)
                throws IOException, InterruptedException {
            Float sum = 0f;
            Float count = 0f;
            for (FloatWritable val : values) {
                sum += val.get();
                count += 1;
            }
            result.set(sum / count);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Milano");
        job.setJarByClass(Milano.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(FloatSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
