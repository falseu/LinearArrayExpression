import java.lang.Math;

public class LinearArithExpression {
	
	/*
	 * limit = [term, unit, op_rm, value]
	 */
	
	public int[] arr;
	private int[] consts;
	private int[] vars;
	private int cSize;
	private int vSize;
	private int M;
	
	public LinearArithExpression(int[] consts, int[] vars) {
		this.consts = consts;
		this.vars = vars;
		cSize = consts.length;
		vSize = vars.length;
		M = cSize + vSize + 1;
	}
	
	public void generate(int op_max) {
		//if (op_max == 0) { return; }
		arr = new int[3 * op_max + 2];
		int[] limit = new int[4];
		limit[0] = Integer.MAX_VALUE;
		limit[1] = M - 2;
		limit[2] = op_max;
		limit[3] = 0;
		TestCallBack tcb = new TestCallBack();
		gen(0, limit, tcb);
	}
	
	public void gen(int index, int[] limit, CallBack cb) {
		gen_sum(index, limit, cb);
		//limit[0] = Integer.MAX_VALUE;
		//gen_minus(index, limit, cb);
	}
	
	public void gen_minus(int index, int[] limit, CallBack cb) {
		gen_term(index, limit, cb);
		if (limit[2] <= 0) { return; }
		gen_term(index, limit, new MinusCallBack(cb));
	}
	
	public void gen_sum(int index, int[] limit, CallBack cb) {
		//int[] lim_cpy = copy(limit);
		gen_term(index, limit, cb);
		if (limit[2] <= 0) { return; }
		gen_term(index, limit, new SumCallBack(cb));
	}
	
	public void gen_term(int index, int[] limit, CallBack cb) {
		//int[] lim_cpy = copy(limit);
		gen_unit(index, limit, cb);
		if (limit[2] <= 0) { return; }
		gen_unit(index, limit, new TermCallBack(cb));
	}
	
	public void gen_unit(int index, int[] limit, CallBack cb) {
		for (int i = 0; i <= Math.min(limit[1], cSize - 1); i++) {
			if (consts[i] == 1 && limit[3] != 0) { continue; }
			int[] lim_cpy = copy(limit);
			lim_cpy[3] = lim_cpy[3] * M + i + 1;
			if (lim_cpy[3] >= lim_cpy[0]) { return; }
			arr[index] = 1;
			arr[index + 1] = consts[i];
			lim_cpy[1] = i;
			cb.call(index + 2, lim_cpy, this);
		}
		
		for (int i = cSize; i <= limit[1]; i++) {
			int[] lim_cpy = copy(limit);
			lim_cpy[3] = lim_cpy[3] * M + i + 1;
			if (lim_cpy[3] >= lim_cpy[0]) { return; }
			arr[index] = 2;
			arr[index + 1] = vars[i - cSize];
			lim_cpy[1] = i;
			cb.call(index + 2, lim_cpy, this);
		}
	}
	
	public int[] getExpression() {
		return arr;
	}
	
	public int[] copy(int[] limit) {
		int[] lim_cpy = new int[limit.length];
		System.arraycopy( limit, 0, lim_cpy, 0, limit.length );
		return lim_cpy;
	}
	
	public int getM() {
		return M;
	}
	
	public int evaluate(int end) {
		Cursor cursor = new Cursor();
		int result = evalTerm(cursor, end);
		while(cursor.getIndex() < end) {
			switch (arr[cursor.getIndex()]) {
			case 3: cursor.addIndex(1);
					result += evalTerm(cursor, end);
					break;
			case 4: cursor.addIndex(1);
					result -= evalTerm(cursor, end);
					break;
			}
		}
		return result;
	}
	
	public int evalTerm(Cursor cursor, int end) {
		int index = cursor.getIndex();
		//if (index >= end) { return 1; }
		int term = read(index);
		cursor.addIndex(2);
		if (cursor.getIndex() >= end) { return term; }
		while(cursor.getIndex() < end 
				&& arr[cursor.getIndex()] == 5) {
			cursor.addIndex(1);
			term *= evalTerm(cursor, end);
		}
		return term;
	}
	
	public int read(int index) {
		if (arr[index] == 1) {
			return arr[index + 1];
		} else if (arr[index] == 2) {
			//TODO: this should be the variable value
			return arr[index + 1];
		}
		return 0;
	}
	
}