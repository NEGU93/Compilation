	.text
	.globl main
main:
	movq $2, %rdi
	call sbrk
	movq $3, %rdi
	call sbrk
	movq %rax, %rcx
	movq $2, %rdi
	call sbrk
	movq %rax, 1(%rcx) 
	movq $65, %rdi
	movq %rdi, 0(%rcx) 
	movq $66, %rdi
	movq %rdi, 2(%rcx) 
	movq 0(%rcx) , %rdi
	call putchar
	movq 2(%rcx) , %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $88, %rdi
	movq %rdi, 0(%rax) 
	movq $89, %rdi
	movq %rdi, 1(%rax) 
	movq 0(%rcx) , %rdi
	call putchar
	movq 2(%rcx) , %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq %rax, 1(%rcx) 
	movq 0(%rcx) , %rdi
	call putchar
	movq 2(%rcx) , %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
