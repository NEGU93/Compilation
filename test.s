<<<<<<< HEAD
.text
.globl main
fact:
movq %rsi, %rdi
movq $1, %rcx
cmpq %rdi, %rcx
jle L7
movq %rsi, %rdi
addq $-1, %rdi
call fact
movq %rsi, %rdi
imulq %rdi, %rax
L1:
ret
L7:
movq $1, %rax
jmp L1
main:
movq $42, %rdi
call fact
movq %rax, %rdi
movq %rdi, %rax
ret
.data
=======
	.text
	.globl main
fact:
	movq %rsi, %rdi
	movq $1, %rcx
	cmpq %rdi, %rcx
	jle L7
	movq %rsi, %rdi
	addq $-1, %rdi
	call fact
	movq %rsi, %rdi
	imulq %rdi, %rax
L1:
	ret
L7:
	movq $1, %rax
	jmp L1
main:
	movq $42, %rdi
	call fact
	movq %rax, %rdi
	movq %rdi, %rax
	ret
	.data
>>>>>>> 9106f33313452716636dd56b401d2c74bce014b0
