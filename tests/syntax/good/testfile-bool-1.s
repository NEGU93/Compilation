	.text
	.globl main
m:
	movq $5, %rax
	movq $6, %rdi
	cmpq %rax, %rdi
	jge L6
	movq $1, %rdi
L5:
	movq $0, %rcx
	movq $0, %rdi
	andq %rdi, %rcx
	orq %rcx, %rax
	ret
L6:
	movq $0, %rdi
	jmp L5
	.data
