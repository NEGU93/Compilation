
struct S { int a; int b; };

struct S *s;

int main() {
  struct S *p;
  s = sbrk(sizeof(struct S));
  p = s;
  p->a = 'A';
  putchar(s->a);
  putchar(p->a);
  p->b = 'B';
  putchar(s->b);
  putchar(p->b);
  putchar(10);
  return 0;
}
