	.text
	.globl main
fact_rec:
	movq %rsi, %rdi
	movq $1, %rcx
	cmpq %rdi, %rcx
	jle L7
	movq %rsi, %rdi
	addq $-1, %rdi
	call fact_rec
	movq %rsi, %rdi
	imulq %rdi, %rax
L1:
	ret
L7:
	movq $1, %rax
	jmp L1
main:
	movq $0, %rdi
	call fact_rec
	movq $1, %rdi
	cmpq %rax, %rdi
	je L28
L26:
	movq $1, %rdi
	call fact_rec
	movq $1, %rdi
	cmpq %rax, %rdi
	je L22
L20:
	movq $5, %rdi
	call fact_rec
	movq $120, %rdi
	cmpq %rax, %rdi
	je L16
L14:
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L16:
	movq $51, %rdi
	call putchar
	jmp L14
	jmp L14
L22:
	movq $50, %rdi
	call putchar
	jmp L20
	jmp L20
L28:
	movq $49, %rdi
	call putchar
	jmp L26
	jmp L26
	.data
