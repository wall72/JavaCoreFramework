package jcf.sua.support.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import jcf.data.RowStatus;
import jcf.sua.exception.ValidationViolationException;
import jcf.sua.ux.UxConstants;
import jcf.sua.validation.ViolationChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * JSR 303 이용하여 모델의 어노테이션을 사용하여 벨리데이션을 선언하면 이를 검증한다.
 * @author mina
 *
 * @param <E>
 */
public  class ModelPropertyViolationChecker<E> implements ViolationChecker<E>{

	private static final Logger logger = LoggerFactory.getLogger(ModelPropertyViolationChecker.class);
	private LocalValidatorFactoryBean validatorFactory;
	private Validator validator;
	private E  bean;


	/**
	 * LocalValidatorFactoryBean 을 생성한다.
	 */
	public  ModelPropertyViolationChecker(){
		if(validatorFactory==null){
			validatorFactory= new LocalValidatorFactoryBean();
			validatorFactory.afterPropertiesSet();
			validator = validatorFactory.getValidator();
		}

	}


	/**
	 * 벨리데이션 위반 사항을 체크한다.
	 *  기본적인 체크와 Group 을 이용한 CRUD 에 따른 벨리테이션 위반사항을 체크하여 ValidationViolationException 를 발생시킨다.
	 */
	public void checkViolations(E bean) {
		this.bean=bean;
		Set<ConstraintViolation<E>> constraintViolations=  checkDefaultModelValidationViolation();
		if(constraintViolations.size()==0){
			constraintViolations=  checkModelValidationViolationByRowStatus();
		}
		if(constraintViolations.size()> 0){
			throw new ValidationViolationException(constraintViolations);
		}
	}


	/**
	 *  모델 property 에 validation 위반에 대해  체크한다.
	 *  CRUD에 구분없이 모든 위반사항에 대해 체크한다.
	 */
	private Set<ConstraintViolation<E>> checkDefaultModelValidationViolation() {
		Set<ConstraintViolation<E>>  constraintViolations = validator.validate(bean);
		return constraintViolations;
	}

	/**
	 * 받아오는 데이터의 rowStatus 상태에 따라 부분적으로 위반사항에 대해 체크한다.
	 * 예로 모델에 Group 을  Group.UPDATE 으로 선언했다면 (@NotEmpty(groups={ Group.UPDATE.class}))) 모델에 바인딩되는 rowStatus 가
	 * UPDATE 일 경우에만 벨리데이션 위반에 대해 체크 할 것이다. INSERT, DELETE 에 대해서도 마찬가지다.
	 * BUT  모델에 Group 을  Group.ALWAYS 으로 선언한 경우에는 무조건적으로 체크할 것이다.
	 * @return
	 */
	private Set<ConstraintViolation<E>>  checkModelValidationViolationByRowStatus() {
		Set<ConstraintViolation<E>> constraintViolations = validator.validate(bean, Group.ALWAYS.class);
		try {
			ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(bean);
			String rowstatus=	(String) propertyAccessor.getPropertyValue(UxConstants.DEFAULT_ROWSTATUS_PROPERTY_NAME);
			if(rowstatus.equalsIgnoreCase(RowStatus.UPDATE.toString())){
				Set<ConstraintViolation<E>> updateViolations = validator.validate(bean, Group.UPDATE.class);
				constraintViolations.addAll(updateViolations);
			}
			else if (rowstatus.equalsIgnoreCase(RowStatus.INSERT.toString())){
				Set<ConstraintViolation<E>> insertViolations = validator.validate(bean, Group.INSERT.class);
				constraintViolations.addAll(insertViolations);
			}
			else if (rowstatus.equalsIgnoreCase(RowStatus.DELETE.toString())){
				Set<ConstraintViolation<E>> deleteViolations = validator.validate(bean, Group.DELETE.class);
				constraintViolations.addAll(deleteViolations);
			}
		} catch (Exception e) {
			logger.warn("the number of constraint violation is "	+ constraintViolations.size());

		}
		return constraintViolations;
	}





}
