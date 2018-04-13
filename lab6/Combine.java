import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Combine {

    public static class Map1 extends Mapper<Object, Text, Text, Text> {
        private Text reputation = new Text();
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll("\"\\s+\"", "\""), "\"");
            while (itr.hasMoreTokens()) {
                String subkey = itr.nextToken();
                word.set(subkey.trim().substring(1));
                String reputationvalue = itr.nextToken().trim();
                if (!reputationvalue.equals("reputation")) {
                    reputation.set(reputationvalue.trim());
                    context.write(word, reputation);
                }
                for (int i = 0; i < 3; i++) {
                    String passed = itr.nextToken();
                    //System.out.println("Passed token is " + passed);
                }
            }
        }
    }

    public static class Map2 extends Mapper<Object, Text, Text, Text> {
        private Text score = new Text();
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString().replaceAll("[\\s]+\"\"[\\s]+", " \"Data\" ").replaceAll("\"\"", "").replaceAll("\"[\\s]+\"", "\"").replaceAll("\"\"", "").replaceAll("\\s+", ""), "\"");

            while (itr.hasMoreTokens()) {
                for (int i = 0; i < 3; i++) {
                    String passed = itr.nextToken();
                    System.out.println("Passed token is " + passed);
                }
                String subkey = itr.nextToken();
                System.out.println("Subkey is: " + subkey);
                word.set(subkey.trim());
                for (int i = 0; i < 4; i++) {
                    String passed = itr.nextToken();
                    System.out.println("Passed token is " + passed);
                }
                String scorevalue = itr.nextToken().trim();
                System.out.println("Value is: " + scorevalue);
                if (!scorevalue.equals("score")) {
                    score.set(scorevalue.trim());
                    context.write(word, score);
                }
            }
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();
        private String reputationAndScore = "";

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values)
                reputationAndScore = reputationAndScore + val.toString() + " ";
            result.set(reputationAndScore);
            context.write(key, result);
        }

    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Combine");
        job.setJarByClass(Combine.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, Map1.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, Map2.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}