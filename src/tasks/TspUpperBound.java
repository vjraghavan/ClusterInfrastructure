package tasks;

import java.io.Serializable;

import system.Shared;

public class TspUpperBound implements Shared<Double>, Serializable {
	private static final long serialVersionUID = 16572096843503373L;
	private Double shared;

	/**
	 * Construct an TspUpperBound from the Double argument.
	 * 
	 * @param shared
	 *            The <CODE>double</CODE> value to be shared.
	 */
	public TspUpperBound(double shared) {
		this.shared = new Double(shared);
	}

	/**
	 * Returns a reference to the shared double Object.
	 * 
	 * @return a reference to the shared double Object.
	 */
	public Double get() {
		return shared;
	}

	/**
	 * This method operationally defines the semantics of <I>newer</I>.
	 * 
	 * @return true if & only if this is newer than the argument Shared Object.
	 * @param shared
	 *            The Shared Object whose value is proposed as OLDER.
	 */
	@Override
	public boolean isNewerThan(Shared<?> shared) {
		if (shared != null) {
			TspUpperBound tspShared = (TspUpperBound) shared;
			return this.shared.doubleValue() < tspShared.get().doubleValue();
		} else
			return true;
	}

}
