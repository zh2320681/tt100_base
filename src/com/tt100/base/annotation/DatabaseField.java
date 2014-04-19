package com.tt100.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.renderscript.Element.DataType;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface DatabaseField
{
  public static final int DEFAULT_MAX_FOREIGN_AUTO_REFRESH_LEVEL = 2;
  public static final String DEFAULT_STRING = "";
  public static final int NO_MAX_FOREIGN_AUTO_REFRESH_LEVEL_SPECIFIED = 255;

  public abstract boolean allowGeneratedIdInsert();

  public abstract boolean canBeNull();

  public abstract String columnDefinition();

  public abstract String columnName();

  public abstract DataType dataType();

  public abstract String defaultValue();

  public abstract boolean foreign();

  public abstract boolean foreignAutoCreate();

  public abstract boolean foreignAutoRefresh();

  public abstract String foreignColumnName();

  public abstract String format();

  public abstract boolean generatedId();

  public abstract String generatedIdSequence();

  public abstract boolean id();

  public abstract boolean index();

  public abstract String indexName();

  public abstract int maxForeignAutoRefreshLevel();

  public abstract boolean throwIfNull();

  public abstract boolean unique();

  public abstract String unknownEnumName();

  public abstract boolean useGetSet();

  public abstract boolean version();

  public abstract int width();
}
