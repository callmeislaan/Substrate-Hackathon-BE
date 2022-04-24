package com.backend.apiserver.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.text.DecimalFormat;

public class FormatUtils {

	public static String transactionIdFormatter(Long transactionId) {
		String transactionIdTemp = "0000000000" + transactionId;
		return transactionIdTemp.substring(transactionIdTemp.length() - 10);
	}

	public static String percentageFormatter(Float floatNumber) {
		DecimalFormat decimalFormat = new DecimalFormat("#.##%");
		return decimalFormat.format(floatNumber);
	}

	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	public static String[] makeArray(String... params) {
		return ArrayUtils.addAll(params);
	}
}
