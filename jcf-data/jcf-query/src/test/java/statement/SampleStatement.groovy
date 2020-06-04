package statement

/*
 * 테스트를 위한 샘플 에스큐엘
 */
class SampleStatement {

	public static def selectProductsWithoutTemplate = '''
		SELECT PRODUCT_ID, PRODUCT_TYPE_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, UPDATED FROM PRODUCT
	'''

	public static def selectProductsWithInterpolation = '''
		#set( $tableName = "PRODUCT")

		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		 FROM ${tableName}
	'''

	public static def selectProductsWithIfStatment = '''
		#set( $tableName = "PRODUCT")

		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		 FROM ${tableName}

		#if (${productId} != 0)
		 WHERE PRODUCT_ID = :productId

		 	#if (${session_productTypeId} != $null)
		 		AND PRODUCT_TYPE_ID = :session_productTypeId
		 	#end
		 AND PRODUCT_ID > 1000
		#else
		 WHERE PRODUCT_ID > 100
		#end
	'''

	public static def selectProductsWithForeachStatment = '''
		#set( $tableName = "PRODUCT")

		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		 FROM ${tableName}

		#if (${productIdList.size()} > 0)
			WHERE PRODUCT_ID IN (
			#foreach($productId in ${productIdList})
				#if ($foreach.index > 0)
					,
				#end

				${productId}
			#end
			)
		#end
	'''

	public static def selectProductTypeWithIfStatment = '''
		SELECT PRODUCT_TYPE_ID,
			PRODUCT_TYPE_NAME,
			PRODUCT_TYPE_DESCRIPTION,
			UPDATED
		 FROM PRODUCT_TYPE

		#if (${productType.productTypeId} > 0)
		 WHERE PRODUCT_ID = :productType.productTypeId
		 AND PRODUCT_ID > 1000
		#end

	'''

	public static def selectProductId = '''
		SELECT PRODUCT_ID FROM PRODUCT WHERE PRODUCT_ID = :productId
	'''

	public static String selectDynamicQuery = '''
		#set( $tableName = "PRODUCT")

		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		 FROM ${tableName}
		 #@dynamic_condition ('group1', 'AND', $productId) PRODUCT_ID = :productId #end
		 #@dynamic_condition ('group1', 'AND', $productTypeId) PRODUCT_TYPE_ID = :productTypeId #end
	'''

	public static String selectQueryWithInMacro = '''
		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		FROM PRODUCT
		#@dynamic_condition ('group1', 'AND', $productTypeId) #in('PRODUCT_ID', ${productId}) #end
		#@dynamic_condition ('group1', 'OR', $productTypeId) PRODUCT_TYPE_ID = :productTypeId #end
	''';

	public static def insertProduct = '''
		INSERT INTO PRODUCT VALUES(:productId, :productTypeId, :productName, :productDescription, sysdate)
	'''

	public static def updateProduct = '''
		UPDATE PRODUCT SET
			PRODUCT_NAME = :productName,
			PRODUCT_DESCRIPTION = :productDescription,
			UPDATED = sysdate
		WHERE PRODUCT_ID = :productId
	'''

	public static def deleteProduct = '''
		DELETE FROM PRODUCT WHERE PRODUCT_ID = :productId
	'''

	/**
	 * 심심해서 프리마커 테스트
	 */
	public static def selectProductUsingFreeMarker = '''
		SELECT PRODUCT_ID,
			PRODUCT_TYPE_ID,
			PRODUCT_NAME,
			PRODUCT_DESCRIPTION,
			UPDATED
		 FROM PRODUCT
		WHERE PRODUCT_ID = :productId

		<#if productId &gt; 1000> AND PRODUCT_ID > 1000 </#if>
	'''
}
