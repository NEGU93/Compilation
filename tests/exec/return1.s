	.text
	.globl main
f:
	jz L9
	call putchar
	movq %rdi, %rcx
	call f
L1:
	ret
L9:
	movq $10, %rax
	jmp L1
main:
	movq $0, %rcx
	movq $67, %rdx
	movq $66, %rsi
	movq $65, %rdi
	call f
	movq %rax, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
