package cn.tt100.base.imageLoader.cache.disc.naming;

/**
 * Names image file as image URI {@linkplain String#hashCode() hashcode}
 */
public class HashCodeFileNameGenerator implements FileNameGenerator {
	@Override
	public String generate(String imageUri) {
		return String.valueOf(imageUri.hashCode());
	}
}
