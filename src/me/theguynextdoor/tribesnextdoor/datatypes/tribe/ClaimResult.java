package me.theguynextdoor.tribesnextdoor.datatypes.tribe;

import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;

public enum ClaimResult {

	NOT_CONNECTED(MessageUtils.CHUNK_NOT_CONNECTED),
	ALREADY_CLAIMED(MessageUtils.CHUNK_ALREADY_CLAIMED),
	NO_PERMISSION(MessageUtils.NOT_CHIEF_OR_ELDER),
	LIMIT_REACHED(MessageUtils.MAX_CLAIMS_REACHED),
	INSUFFICIENT_FUNDS(MessageUtils.INSUFFICIENT_FUNDS),
	SUCCESS(MessageUtils.CHUNK_CLAIM_SUCCESS);

	private String message;

	private ClaimResult(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
