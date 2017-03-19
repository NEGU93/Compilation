	.text
	.globl main
fact_imp:
	movq $1, %rax
L13:
	movq %rsi, %rdi
	movq $1, %rcx
	cmpq %rdi, %rcx
	jg L10
	ret
L10:
	movq %rsi, %rdi
	addq $-1, %rdi
	movq %rdi, %rsi
	addq $1, %rdi
	imulq %rax, %rdi
	movq %rdi, %rax
	jmp L13
	jmp L13
main:
	movq $0, %rdi
	call fact_imp
	movq $1, %rdi
	cmpq %rax, %rdi
	je L33
L31:
	movq $1, %rdi
	call fact_imp
	movq $1, %rdi
	cmpq %rax, %rdi
	je L27
L25:
	movq $5, %rdi
	call fact_imp
	movq $120, %rdi
	cmpq %rax, %rdi
	je L21
L19:
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L21:
	movq $51, %rdi
	call putchar
	jmp L19
	jmp L19
L27:
	movq $50, %rdi
	call putchar
	jmp L25
	jmp L25
L33:
	movq $49, %rdi
	call putchar
	jmp L31
	jmp L31
	.data
