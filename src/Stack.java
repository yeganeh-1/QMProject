public class Stack {
    char[] charArray = new char[100];
    int top;
    Stack() {
        top = -1;
    }
    public void push(char ch) {
        charArray[++top] = ch;
    }
    public void pop() {
        top--;
    }
    public char top() {
        if (top == -1) {
            return ' ';
        }
        return charArray[top];
    }
    public boolean isEmpty() {
        return top == -1;
    }
}

