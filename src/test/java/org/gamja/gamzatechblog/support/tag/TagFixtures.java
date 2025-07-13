package org.gamja.gamzatechblog.support.tag;

import java.util.ArrayList;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;

public final class TagFixtures {
	private TagFixtures() {

	}

	public static final Tag SPRING = tag("spring");
	public static final Tag DOCKER = tag("docker");

	//Fake레포 사용 예정이라 Id는 null값
	public static Tag tag(String name) {
		return new Tag(null, name, new ArrayList<>());
	}

}
