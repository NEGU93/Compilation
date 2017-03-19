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
	movq %rdi, %rcx
L25:
	movq $4, %rdi
	cmpq %rcx, %rdi
	jle L22
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L22:
	movq %rcx, %rdi
	call fact
	addq $65, %rdi
	call putchar
	movq %rcx, %rdi
	addq $1, %rdi
	movq %rdi, %rcx
	jmp L25
	jmp L25
	.data
