package eu.dzhw.fdz.metadatamanagement.domain.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Converter for new JSR310 objects and old Date objects.
 */
public final class Jsr310DateConverters {

  private Jsr310DateConverters() {}

  /**
   * LocalDate -> Date.
   */
  public static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

    public static final LocalDateToDateConverter INSTANCE = new LocalDateToDateConverter();

    private LocalDateToDateConverter() {}

    @Override
    public Date convert(LocalDate source) {
      return source == null ? null : Date.from(source.atStartOfDay(ZoneId.systemDefault())
        .toInstant());
    }
  }

  /**
   * Date -> LocalDate.
   */
  public static class DateToLocalDateConverter implements Converter<Date, LocalDate> {
    public static final DateToLocalDateConverter INSTANCE = new DateToLocalDateConverter();

    private DateToLocalDateConverter() {}

    @Override
    public LocalDate convert(Date source) {
      return source == null ? null
          : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault())
            .toLocalDate();
    }
  }

  /**
   * ZonedDateTime -> Date.
   */
  public static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
    public static final ZonedDateTimeToDateConverter INSTANCE = new ZonedDateTimeToDateConverter();

    private ZonedDateTimeToDateConverter() {}

    @Override
    public Date convert(ZonedDateTime source) {
      return source == null ? null : Date.from(source.toInstant());
    }
  }

  /**
   * Date -> ZonedDateTime.
   */
  public static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
    public static final DateToZonedDateTimeConverter INSTANCE = new DateToZonedDateTimeConverter();

    private DateToZonedDateTimeConverter() {}

    @Override
    public ZonedDateTime convert(Date source) {
      return source == null ? null
          : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
    }
  }

  /**
   * LocalDateTime -> Date.
   */
  public static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
    public static final LocalDateTimeToDateConverter INSTANCE = new LocalDateTimeToDateConverter();

    private LocalDateTimeToDateConverter() {}

    @Override
    public Date convert(LocalDateTime source) {
      return source == null ? null : Date.from(source.atZone(ZoneId.systemDefault())
        .toInstant());
    }
  }

  /**
   * Date -> LocalDateTime.
   */
  public static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
    public static final DateToLocalDateTimeConverter INSTANCE = new DateToLocalDateTimeConverter();

    private DateToLocalDateTimeConverter() {}

    @Override
    public LocalDateTime convert(Date source) {
      return source == null ? null
          : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
    }
  }
}
