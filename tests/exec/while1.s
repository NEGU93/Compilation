	.text
	.globl main
main:
	movq $10, %rdi
	movq %rdi, %rcx
L13:
	movq %rcx, %rdi
	addq $-1, %rdi
	movq %rdi, %rcx
	addq $1, %rdi
	jz L4
	movq %rcx, %rdi
	addq $65, %rdi
	call putchar
	jmp L13
	jmp L13
L4:
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
