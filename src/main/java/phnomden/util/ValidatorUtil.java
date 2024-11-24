package phnomden.util;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j2;
import phnomden.common.ErrorCode;
import phnomden.dto.Data;
import phnomden.exception.ApplicationException;

@Log4j2
public class ValidatorUtil {
	/**
	 * @param sample <pre>
	 * String [][] requireFields = {
	 *			{"authorization", authorization},
	 *			{"accountNumber", request.getAccountNumber()},
	 *	}; </pre>
	 */
    public static void validateRequireFields(String[][] requireFields) throws Exception {
        for (String[] fields: requireFields) {
            if (fields[1] == null) { // fields[0]=> name, fields[1]=> value
                log.error(String.format("<<<< Error: Field %s is null.", fields[0]));
                throw new ApplicationException("");
            } else if (fields[1].isEmpty()) {
                log.error(String.format("<<<< Error: Field %s is empty.", fields[0]));
                throw new ApplicationException("");
            }
        }
    }

    /**
     * @param sample <pre>
     * validateRequireFields(data, "fieldName1", "fieldName2", fieldName3, ..);
     * </pre>
     */
    public static void validateRequireFields(Data data, String...fieldNames) throws Exception {
        if (data == null) {
            log.error("<<<< Error: Object data is null.");
            throw new ApplicationException(ErrorCode.GENERAL_FAIL);
        }
        for (String key: fieldNames) {
            if (StringUtils.isBlank(data.getString(key))) {
                log.error(String.format("<<<< Error: Field %s is blank.", key));
                throw new ApplicationException(ErrorCode.GENERAL_FAIL);
            }
        }
    }
}
