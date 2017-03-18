	.text
	.globl main
f:
	movq %rax, %rdi
	jz L2
	movq %rax, %rdi
	call putchar
	movq %rax, %rdi
	movq %rax, %rdi
	movq %rax, %rdi
	movq %rax, %rdi
	call f
L1:
	ret
L2:
	movq $0, %rax
	jmp L1
main:
	movq $0, %rdi
	movq $67, %rdi
	movq $66, %rdi
	movq $65, %rdi
	call f
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
