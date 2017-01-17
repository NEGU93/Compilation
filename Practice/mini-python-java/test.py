def fibaux(a, b, k):
    if k == 0:
        return a
    else:
        return fibaux(b, a+b, k-1)

def fib(n):
    return fibaux(0, 1, n)

print "quelques valeurs de la suite de Fibonacci :"
for n in [0, 1, 11, 42]:
    print fib(n)
