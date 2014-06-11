package cn.shrek.base.imageLoader.cache.disc.naming;

/**
 * Generates names for files at disk cache
 */
public interface FileNameGenerator {

	/** Generates unique file name for image defined by URI */
	String generate(String imageUri);
}
