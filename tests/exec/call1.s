	.text
	.globl main
f:
	movq %rcx, %rdi
	call putchar
	movq $0, %rax
	ret
main:
	movq $66, %rdi
	movq $65, %rdi
	call f
	movq $65, %rdi
	movq $66, %rdi
	call f
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
