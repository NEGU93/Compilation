	.text
	.globl main
main:
	movq $10, %rdi
L14:
	movq $0, %rcx
	cmpq %rdi, %rcx
	jg L11
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L11:
	addq $-1, %rdi
	addq $65, %rdi
	addq $1, %rdi
	call putchar
	jmp L14
	jmp L14
	.data
