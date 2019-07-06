public class MinusCallBack implements CallBack {
	
	public CallBack cb;
	
	public void call(int index, int[] limit, LinearArithExpression exp) {
		if (limit[2] <= 0) { return; }
		limit[1] = exp.getM() - 2;
		limit[2] = limit[2] - 1;
		exp.arr[index] = 4;
		limit[3] = 0;
		exp.gen_minus(index + 1, limit, cb);
	}
}