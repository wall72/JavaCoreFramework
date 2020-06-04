package statement

class WordDeungrokQry {
	public static def selectWord(){
		String strSql = '''
			select	'0' chk,
					seq,
					daneo_nm,
					yeongmun_seolmyeong,
					hanja_seolmyeong,
					chulcheo,
					bunya
			  from  t_daneosajeon
			 where  1 = 1
			   and  daneo_nm like :daneoNm || '%'
		'''

		return strSql
	}

	public static def insertWord(){
		String strSql = '''
			insert into t_daneosajeon (
										seq,
										daneo_nm,
										yeongmun_seolmyeong,
										hanja_seolmyeong,
										chulcheo,
										bunya
									   )
							   values (
										   (select max(nvl(seq,0))+1 from t_daneosajeon),
										   :daneoNm,
										   :yeongmunSeolmyeong,
										   :hanjaSeolmyeong,
										   :chulcheo,
										   :bunya
										 )
		'''

		return strSql
	}

	public static def updateWord(){
		String strSql = '''
			update	t_daneosajeon
			   set  daneo_nm = :daneo_nm,
					yeongmun_seolmyeong = :yeongmunSeolmyeong
			 where  1 = 1
			   and  seq = :seq
		'''

		return strSql
	}

	public static def deleteWord(){
		String strSql = '''
			delete from t_daneosajeon
			 where  1 = 1
			   and  seq = :seq
		'''

		return strSql
	}

	public static def selectWord = '''
			select	'0' chk,
					seq,
					daneo_nm,
					yeongmun_seolmyeong,
					hanja_seolmyeong,
					chulcheo,
					bunya
			  from  t_daneosajeon
			 where  1 = 1
			   and  daneo_nm like :daneoNm || '%'
	'''

	public static def insertWord = '''
			insert into t_daneosajeon (
										seq,
										daneo_nm,
										yeongmun_seolmyeong,
										hanja_seolmyeong,
										chulcheo,
										bunya
									   )
							   values (
										   (select max(nvl(seq,0))+1 from t_daneosajeon),
										   :daneoNm,
										   :yeongmunSeolmyeong,
										   :hanjaSeolmyeong,
										   :chulcheo,
										   :bunya
										 )
	'''

	public static def updateWord = '''
			update	t_daneosajeon
			   set  daneo_nm = :daneo_nm,
					yeongmun_seolmyeong = :yeongmunSeolmyeong
			 where  1 = 1
			   and  seq = :seq
	'''

	public static def deleteWord = '''
			delete from t_daneosajeon
			 where  1 = 1
			   and  seq = :seq
	'''
}
