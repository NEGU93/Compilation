	.text
	.globl main
f:
	movq %rax, %rdi
	jz L9
	movq %rax, %rdi
	call putchar
	movq %rax, %rdi
	movq %rax, %rdi
	movq %rax, %rdi
	movq %rax, %rdi
	call f
L1:
	ret
L9:
	movq $10, %rax
	jmp L1
main:
	movq $0, %rdi
	movq $67, %rdi
	movq $66, %rdi
	movq $65, %rdi
	call f
	call putchar
	movq $0, %rax
	ret
	.data
