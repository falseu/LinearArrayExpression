import java.util.ArrayList;

/*
 * 0 : pass		-4 : !=
 * -1 : &&		-5 : <
 * -2 : ||		-6 : >
 * -3 : ==
 */

public class LinearLogicExpression {
	
	public int[] arr;
	private int[] consts;
	private int[] vars;
	private int cSize;
	private int vSize;
	private int M;
	public ArrayList<int[]> lst;
	
	public LinearLogicExpression(int[] consts, int[] vars) {
		this.consts = consts;
		this.vars = vars;
		cSize = consts.length;
		vSize = vars.length;
		M = cSize + vSize + 1;
		lst = new ArrayList<>();
	}
	
	public void generate() {
		//TODO
		arr = new int[25];
		gen_arith();
		TestCallBack tcb = new TestCallBack();
		gen_bool(0, Integer.MAX_VALUE, tcb);
		arr[0] = -1;
		gen_bool(1, Integer.MAX_VALUE, new LogicCallBack(tcb));
		arr[0] = -2;
		gen_bool(1, Integer.MAX_VALUE, new LogicCallBack(tcb));
	}
	
	public void gen_bool(int index, int limit, CallBack cb) {
		int curr = 0;
		for (int i = 0; i < lst.size(); i++) {
			for (int j = 0; j < i; j++) {
				curr = (i + 1) * (lst.size()) + (j + 1);
				if (curr >= limit) { return; }
				int[] left = lst.get(i);
				int[] right = lst.get(j);
				System.arraycopy(left, 0, arr, index + 1, left.length);
				System.arraycopy(right, 0, arr, index + 1 + left.length, right.length);
				for (int k = -3; k >= -6; k--) {
					arr[index] = k;
					cb.call(index + left.length + right.length + 1, curr, this);
				}
			}
		}
	}
	
	public void gen_arith() {
		for (int i = 0; i < vSize; i++) {
			int[] curr = new int[2];
			curr[0] = 2;
			curr[1] = vars[i];
			lst.add(curr);
		}
		
		for (int i = 0; i < vSize; i++) {
			for (int j = 0; j < cSize; j++) {
				int[] plus = new int[5];
				plus[0] = 3;
				plus[1] = 2;
				plus[2] = vars[i];
				
				int[] minus = new int[5];
				minus[0] = 4;
				minus[1] = 2;
				minus[2] = vars[i];
				
				plus[3] = 1;
				plus[4] = consts[j];
				lst.add(plus);
				minus[3] = 1;
				minus[4] = consts[j];
				lst.add(minus);
			}
			
		}
	}
	
	public int[] getExpression() {
		return arr;
	}
	
	public boolean evaluate(Cursor cursor) {
		boolean result;
		int index = cursor.getIndex();
		cursor.addIndex(1);
		if (arr[index] == -1) {
			result = evaluate(cursor);
			if (result == false) { return false; }
			return result && evaluate(cursor);
		} else if (arr[index] == -2) {
			result = evaluate(cursor);
			if (result == true) { return true; }
			return result || evaluate(cursor);
		} else {
			int left = 0;
			left = eval_arith(cursor);
			switch(arr[index]) {
			case -3: return left == eval_arith(cursor);
			case -4: return left != eval_arith(cursor);
			case -5: return left < eval_arith(cursor);
			case -6: return left > eval_arith(cursor);
			}
		}
		return false;
	}
	
	public int eval_arith(Cursor cursor) {
		int index = cursor.getIndex();
		int result;
		switch(arr[index]) {
		case 1: cursor.addIndex(2);
				return arr[index + 1];
		case 2: cursor.addIndex(2);
				return arr[index + 1];
		case 3: cursor.addIndex(1);
				result = eval_arith(cursor);
				return result + eval_arith(cursor);
		case 4: cursor.addIndex(1);
				result = eval_arith(cursor);
				return result - eval_arith(cursor);
		}
		return 0;
	}
	
}