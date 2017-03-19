	.text
	.globl main
f:
	movq %rcx, %rdi
	jz L2
	movq %rcx, %rdi
	call putchar
	call f
L1:
	ret
L2:
	movq $0, %rax
	jmp L1
main:
	movq $0, %rcx
	movq $67, %rdx
	movq $66, %rsi
	movq $65, %rdi
	call f
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
