package helper;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * 테스트 용 데이터 초기화 지원 클래스
 *
 */
public class DataStoreInitializer implements InitializingBean, BeanFactoryAware {

    public void afterPropertiesSet() throws Exception {

    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        DataSource dataSource = (DataSource) beanFactory.getBean("dataSource");

        SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);

        try {
            template.update("INSERT INTO PRODUCT_TYPE VALUES('CARRIERS', '선박 타입', '선박 명', sysdate)");
        } catch (DataAccessException e) {
        }

        try {
            template
                    .update("INSERT INTO PRODUCT VALUES(1001, 'CARRIERS', '살물선', '곡물 ·광석 ·석탄 등을 포장하지 않고 그대로 선창에 싣고 수송하는 화물선', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
                    .update("INSERT INTO PRODUCT VALUES(1002, 'CARRIERS', '유조선', '석유류·경유·당밀·포도주원액·화공약품 및 액화석유가스(LPG)·액화천연가스(LNG) 등 액체화물을 용기에 넣지 않은 비포장 상태로 산적하여 대량수송하는 선박', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template.update("INSERT INTO PRODUCT VALUES(1003, 'CARRIERS', '컨테이너선', '컨테이너를 수송하는 선박', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
            	.update("INSERT INTO PRODUCT VALUES(1004, 'CARRIERS', 'FPSO선', 'Floating Production Storage Offloading, 부유식 원유생산저장 설비', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
            	.update("INSERT INTO PRODUCT VALUES(1005, 'CARRIERS', 'FPSO선', 'Floating Production Storage Offloading, 부유식 원유생산저장 설비', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
            	.update("INSERT INTO PRODUCT VALUES(1006, 'CARRIERS', 'FPSO선', 'Floating Production Storage Offloading, 부유식 원유생산저장 설비', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
            	.update("INSERT INTO PRODUCT VALUES(1007, 'CARRIERS', 'FPSO선', 'Floating Production Storage Offloading, 부유식 원유생산저장 설비', sysdate)");
        } catch (DataAccessException e) {
        }
        try {
            template
            	.update("INSERT INTO PRODUCT VALUES(1008, 'CARRIERS', 'FPSO선', 'Floating Production Storage Offloading, 부유식 원유생산저장 설비', sysdate)");
        } catch (DataAccessException e) {
        }
    }

}
