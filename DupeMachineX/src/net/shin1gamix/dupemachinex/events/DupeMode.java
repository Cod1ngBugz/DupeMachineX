package net.shin1gamix.dupemachinex.events;

public enum DupeMode {

	BLACKLIST(0),

	WHITELIST(1);

	private final int mode;

	DupeMode(final int mode) {
		this.mode = mode;

	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	public static DupeMode getModeByInt(final int input) {
		for (final DupeMode mode : values()) {
			if (mode.getMode() == input) {
				return mode;
			}
		}
		return DupeMode.BLACKLIST;
	}
}
