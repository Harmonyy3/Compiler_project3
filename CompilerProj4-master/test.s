	.text
main:
main_framesize=0
L1:
	move $t0 $t1
	move $t2 $t1
	move $t3 $t1
	li $t4 2
	move $t0 $t4
	li $t5 3
	move $t2 $t5
	li $t6 10
	move $t0 $t6
	li $t7 5
	move $t3 $t7
	li $t8 4
	move $t2 $t8
	li $t9 7
	move $t3 $t9
	li $t10 20
	move $t0 $t10
	li $t11 4
	move $t2 $t11
	li $t12 7
	move $t2 $t12
	li $t13 8
	move $t0 $t13
	move $t14 $t1
main_ret:
	b L0
L0:


	li $v0, 10
	syscall

