package org.gamja.gamzatechblog.domain.post.util.github;

public enum GitAction {
	ADD("Add"),
	UPDATE("Update"),
	DELETE("Delete");

	public final String value;

	GitAction(String value) {
		this.value = value;
	}
}
