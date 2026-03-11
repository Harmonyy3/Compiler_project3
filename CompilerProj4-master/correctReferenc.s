	.text
null:
null_framesize=0
testStuff:
	add $v0 $a0 2
	jr $ra 
main:
	li $t5 7
	move $t6 $t5
	add $t7 $t6 5
	move $t6 $t7
	sub $t8 $t6 12
	move $t6 $t8
L1:
	blt $t6 15 L2
L0:
	b L3
L2:
	move $a0 $t6
	jal testStuff
	move $t6 $v0
	b L1
L3:
	jr $ra
	nop
