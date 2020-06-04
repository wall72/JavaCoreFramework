package jcf.sua.mvc.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

/**
 * 
 * @author nolang
 * 
 */
public class CrossSiteScriptValidator implements ConstraintValidator<CrossSiteScript, String> {

	private Pattern pattern = Pattern.compile("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>");
	
	/*
	 * (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	public void initialize(CrossSiteScript constraintAnnotation) {

	}

	/*
	 * (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if (!StringUtils.hasText(value)) {
			return true;
		}

		/*
		 * @TODO 현재는 html tag가 입력되었을 경우 위반으로 판단함. 나중에 필요할 경우 보충...
		 */
		return !pattern.matcher(value).find();
	}

}
