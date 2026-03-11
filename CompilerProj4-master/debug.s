# Control Flow Graph: 
0: $t0 <= $t1 ; goto 1 
1: $t2 <= $t1 ; goto 2 
2: $t3 <= $t1 ; goto 3 
3: $t4 <- ; goto 4 
4: $t0 <= $t4 ; goto 5 
5: $t5 <- ; goto 6 
6: $t2 <= $t5 ; goto 7 
7: $t6 <- ; goto 8 
8: $t0 <= $t6 ; goto 9 
9: $t7 <- ; goto 10 
10: $t3 <= $t7 ; goto 11 
11: $t8 <- ; goto 12 
12: $t2 <= $t8 ; goto 13 
13: $t9 <- ; goto 14 
14: $t3 <= $t9 ; goto 15 
15: $t10 <- ; goto 16 
16: $t0 <= $t10 ; goto 17 
17: $t11 <- ; goto 18 
18: $t2 <= $t11 ; goto 19 
19: $t12 <- ; goto 20 
20: $t2 <= $t12 ; goto 21 
21: $t13 <- ; goto 22 
22: $t0 <= $t13 ; goto 23 
23: $t14 <= $t1 ; goto 24 
24: <- ; goto 25 
25: <- $t14 $t1 $t15 $t16 $t17 $t18 $fp $sp $ra $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t27 ; goto 26 
26: <- $t14 $t1 $t15 $t16 $t17 $t18 $fp $sp $ra $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t27 ; goto 
# Interference Graph: 
$t0: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t1: $t13 $t12 $t11 $t10 $t9 $t8 $t3 $t7 $t6 $t2 $t5 $t0 $t4 
$t2: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t3: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t4: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t5: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t6: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t7: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t8: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t9: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t10: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t11: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t12: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t13: $t1 $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t14: $fp $sp $ra $t15 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t16 $t17 $t18 $t27 
$t15: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t16: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t17: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t18: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$fp: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$sp: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$ra: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t19: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t20: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t21: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t22: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t23: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t24: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t25: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t26: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t27: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t14 <= $t1
$t0 <= $t13
$t2 <= $t12
$t2 <= $t11
$t0 <= $t10
$t3 <= $t9
$t2 <= $t8
$t3 <= $t7
$t0 <= $t6
$t2 <= $t5
$t0 <= $t4
$t3 <= $t1
$t2 <= $t1
$t0 <= $t1
# Register Allocation: 
$t0->null
$t1->null
$t2->null
$t3->null
$t4->null
$t5->null
$t6->null
$t7->null
$t8->null
$t9->null
$t10->null
$t11->null
$t12->null
$t13->null
$t14->null
$t15->null
$t16->null
$t17->null
$t18->null
$fp->null
$sp->null
$ra->null
$t19->null
$t20->null
$t21->null
$t22->null
$t23->null
$t24->null
$t25->null
$t26->null
$t27->null
After temp Realloc
# Control Flow Graph: 
0: $t0 <= $t1 ; goto 1 
1: $t2 <= $t1 ; goto 2 
2: $t3 <= $t1 ; goto 3 
3: $t4 <- ; goto 4 
4: $t0 <= $t4 ; goto 5 
5: $t5 <- ; goto 6 
6: $t2 <= $t5 ; goto 7 
7: $t6 <- ; goto 8 
8: $t0 <= $t6 ; goto 9 
9: $t7 <- ; goto 10 
10: $t3 <= $t7 ; goto 11 
11: $t8 <- ; goto 12 
12: $t2 <= $t8 ; goto 13 
13: $t9 <- ; goto 14 
14: $t3 <= $t9 ; goto 15 
15: $t10 <- ; goto 16 
16: $t0 <= $t10 ; goto 17 
17: $t11 <- ; goto 18 
18: $t2 <= $t11 ; goto 19 
19: $t12 <- ; goto 20 
20: $t2 <= $t12 ; goto 21 
21: $t13 <- ; goto 22 
22: $t0 <= $t13 ; goto 23 
23: $t14 <= $t1 ; goto 24 
24: <- ; goto 25 
25: <- $t14 $t1 $t15 $t16 $t17 $t18 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t30 ; goto 26 
26: <- $t14 $t1 $t15 $t16 $t17 $t18 $t19 $t20 $t21 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t30 ; goto 
# Interference Graph: 
$t0: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t1: $t13 $t12 $t11 $t10 $t9 $t8 $t3 $t7 $t6 $t2 $t5 $t0 $t4 
$t2: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t3: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t4: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t5: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t6: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t7: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t8: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t9: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t10: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t11: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t12: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t13: $t1 $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t14: $t19 $t20 $t21 $t15 $t22 $t23 $t24 $t25 $t26 $t27 $t28 $t29 $t16 $t17 $t18 $t30 
$t15: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t16: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t17: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t18: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t19: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t20: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t21: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t22: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t23: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t24: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t25: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t26: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t27: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t28: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t29: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t30: $t14 $t13 $t12 $t11 $t10 $t9 $t8 $t7 $t6 $t5 $t4 $t3 $t2 $t0 
$t14 <= $t1
$t0 <= $t13
$t2 <= $t12
$t2 <= $t11
$t0 <= $t10
$t3 <= $t9
$t2 <= $t8
$t3 <= $t7
$t0 <= $t6
$t2 <= $t5
$t0 <= $t4
$t3 <= $t1
$t2 <= $t1
$t0 <= $t1
# Register Allocation: 
$t0->null
$t1->null
$t2->null
$t3->null
$t4->null
$t5->null
$t6->null
$t7->null
$t8->null
$t9->null
$t10->null
$t11->null
$t12->null
$t13->null
$t14->null
$t15->null
$t16->null
$t17->null
$t18->null
$t19->null
$t20->null
$t21->null
$t22->null
$t23->null
$t24->null
$t25->null
$t26->null
$t27->null
$t28->null
$t29->null
$t30->null
