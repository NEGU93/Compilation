	.text
	.globl main
fact:
	movq %rdi, %rsi
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
	ret
	.data
