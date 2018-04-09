* Ma tran 6x6:
- Bien dich:

mpicc -o matrix_collective matrix_collective

- Chay chuong ttinh MPI

mpirun -np 6 ./matrix_collective

-Ket qua:

[1510216@l01ip7 lab4]$ mpirun -np 6 ./matrix_collective


Result =
  135     107     106     105     104     85
  217     170     186     202     218     180
  110     98      104     110     116     113
  237     188     193     198     203     163
  205     163     184     205     226     175
  103     82      97      112     127     97

