	.data
true: .asciiz "true"
false: .asciiz "false"
str_f_: .asciiz "f_"
str_61: .asciiz "="
str_92n: .asciiz "\n"
# vtables_section
	.data
Fibonacci_vtable:
	.word Fibonacci_sucesion_fib
	.word Fibonacci_out_idx
	.word Fibonacci_out_val
	.text
	.globl main
	 j main

IO_out_str:
	sw $ra, 0($fp)
	lw $a0, -12($fp)
	li $v0, 4
	syscall
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_out_i32:
	sw $ra, 0($fp)
	lw $a0, -12($fp)
	li $v0, 1
	syscall
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_out_bool:
	sw $ra, 0($fp)
	lw $a0, -12($fp)
	li $t0, 1
	beq $a0, $t0, IO_out_bool_true_case
	la $a0, false
	j IO_out_bool_end
IO_out_bool_true_case:
	la $a0, true
IO_out_bool_end:
	li $v0, 4
	syscall
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_out_char:
	sw $ra, 0($fp)
	lw $a0, -12($fp)
	li $v0, 11
	syscall
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_out_array:
IO_in_str:
IO_in_i32:
	sw $ra, 0($fp)
	li $v0, 5
	syscall
	addiu $sp, $sp, 8
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_in_bool:
	sw $ra, 0($fp)
	li $v0, 5
	syscall
	addiu $sp, $sp, 8
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	jr $ra
IO_in_Char:
Str_length:
Str_concat:
Str_substr:
Array_length:
Fibonacci_Fibonacci:
	sw $ra, 0($fp)
	lw $s0, -8($fp)
	la $a0, Fibonacci_vtable
	sw $a0, 0($s0)
# DECL_VARS_LOCALES
	addiu $sp, $sp, 0
# FIN_DECL_VARS_LOCALES
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 8
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 12
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 4
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
	lw $a0, -8($fp) #
	addiu $sp, $sp, 8
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	lw $ra, 0($sp)
	jr $ra
Fibonacci_sucesion_fib:
	sw $ra, 0($fp)
	lw $s0, -8($fp)
# DECL_VARS_LOCALES
	addiu $sp, $sp, -4
# FIN_DECL_VARS_LOCALES
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 8
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 12
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 1
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 4
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -16
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	li $a0, 0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# WHILE
while_0:
	move $a0, $fp
	addiu $a0, $a0, -16
	lw $a0, ($a0)
	sw $a0, ($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -12
	lw $a0, ($a0)
	lw $t0, 4($sp)
	addiu $sp, $sp, 4
	sle $a0, $t0, $a0
	beq $a0, $zero, end_while_0
# LLamado 
	la $t0, Fibonacci_vtable
	lw $t1, 4($t0)
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -16
	lw $a0, ($a0)
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jalr $t1
# LLamado 
	la $t0, Fibonacci_vtable
	lw $t1, 8($t0)
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 8
	lw $a0, ($a0)
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jalr $t1
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 4
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 8
	lw $a0, ($a0)
	sw $a0, ($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 12
	lw $a0, ($a0)
	lw $t0, 4($sp)
	addiu $sp, $sp, 4
	add $a0, $t0, $a0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 8
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 12
	lw $a0, ($a0)
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 12
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	move $a0, $fp
	addiu $a0, $a0, -8
	lw $s0, ($a0)
	addiu $a0, $s0, 4
	lw $a0, ($a0)
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -16
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
	move $a0, $fp
	addiu $a0, $a0, -16
	lw $a0, ($a0)
	sw $a0, ($sp)
	addiu $sp, $sp, -4
	li $a0, 1
	lw $t0, 4($sp)
	addiu $sp, $sp, 4
	add $a0, $t0, $a0
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $a0, ($t1)
#FIN_ASIGNACION
	j while_0
end_while_0:
# END_WHILE
	addiu $sp, $sp, 16
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	lw $ra, 0($sp)
	jr $ra
Fibonacci_out_idx:
	sw $ra, 0($fp)
	lw $s0, -8($fp)
# DECL_VARS_LOCALES
	addiu $sp, $sp, 0
# FIN_DECL_VARS_LOCALES
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	la $a0, str_f_
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jal IO_out_str
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -12
	lw $a0, ($a0)
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jal IO_out_i32
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	la $a0, str_61
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jal IO_out_str
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	lw $ra, 0($sp)
	jr $ra
Fibonacci_out_val:
	sw $ra, 0($fp)
	lw $s0, -8($fp)
# DECL_VARS_LOCALES
	addiu $sp, $sp, 0
# FIN_DECL_VARS_LOCALES
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -12
	lw $a0, ($a0)
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jal IO_out_i32
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -8($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	la $a0, str_92n
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jal IO_out_str
	addiu $sp, $sp, 12
	lw $fp, 0($sp)
	addiu $sp, $sp, 4
	lw $ra, 0($sp)
	jr $ra
main:
	move $fp, $sp
	addiu $sp, $sp, -12
# DECL_VARS_LOCALES
	addiu $sp, $sp, -8
	li $a0, 16
	li $v0, 9
	syscall
	sw $v0, 8($sp)
# FIN_DECL_VARS_LOCALES
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -12
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -12($fp)
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 12
	jal Fibonacci_Fibonacci
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $v0, ($t1)
#FIN_ASIGNACION
# ASIGNACION
# LADO IZQUIERDO
	move $a0, $fp
	addiu $a0, $a0, -16
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
# LADO DERECHO
# LLamado 
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -12($fp)
	li $t0, 0
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 12
	jal IO_in_i32
	lw $t1, 4($sp)
	addiu $sp, $sp, 4
	sw $v0, ($t1)
#FIN_ASIGNACION
# LLamado 
	la $t0, Fibonacci_vtable
	lw $t1, 0($t0)
	addiu $sp, $sp, -4
	sw $fp, 0($sp)
	addiu $sp, $sp, -4
	lw $t0, -12($fp)
	sw $t0, 0($sp)
	addiu $sp, $sp, -4
	move $a0, $fp
	addiu $a0, $a0, -16
	lw $a0, ($a0)
	sw $a0, 0($sp)
	addiu $sp, $sp, -4
	addiu $fp, $sp, 16
	jalr $t1
	addiu $sp, $sp, 16
	addiu $sp, $sp, 4
	li $v0, 10
	syscall
