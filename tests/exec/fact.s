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
	movq $0, %rdi
L25:
	movq $4, %rdi
	cmpq %rdi, %rdi
	jle L22
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L22:
	call fact
	movq %rax, %rdi
	addq $65, %rdi
	call putchar
	addq $1, %rdi
	jmp L25
	jmp L25
	.data
