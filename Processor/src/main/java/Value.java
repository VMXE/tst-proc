
public class Value {
	
	Double bill_amount;
	Double disallowed_amt;
	String updateFlag;
	
	public Value(Double bill_amount, Double disallowed_amt,String updateFlag) {
		super();
		this.bill_amount = bill_amount;
		this.disallowed_amt  = disallowed_amt;
		this.updateFlag = updateFlag;
	}
	
	
	
	public Double getBill_amount() {
		return bill_amount;
	}
	public void setBill_amount(Double bill_amount) {
		this.bill_amount = bill_amount;
	}
	public Double getdisallowed_amt() {
		return disallowed_amt;
	}
	public void setDeductible_amt(Double disallowed_amt) {
		this.disallowed_amt  = disallowed_amt;
	}
	
	public String getupdateFlag() {
		return updateFlag;
	}
	public void setupdateFlag(String updateFlag) {
		this.updateFlag  = updateFlag;
	}
	

}
