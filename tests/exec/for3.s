	.text
	.globl main
main:
	movq $10, %rdi
	movq %rdi, %rcx
L12:
	movq %rcx, %rdi
	jz L4
	movq %rcx, %rdi
	addq $-1, %rdi
	movq %rdi, %rcx
	addq $65, %rdi
	call putchar
	jmp L12
	jmp L12
L4:
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data