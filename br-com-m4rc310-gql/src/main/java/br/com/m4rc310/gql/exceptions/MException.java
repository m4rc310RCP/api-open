package br.com.m4rc310.gql.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "to")
public class MException extends Exception {
	private static final long serialVersionUID = 8768531949753036256L;
	private int code;
	private String message;
}
