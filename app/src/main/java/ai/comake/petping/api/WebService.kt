package ai.comake.petping.api

import ai.comake.petping.data.vo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface WebService {
    /**
     * 앱 버전을 가져온다.(토큰이 없는 경우)
     */
    @GET("/v1/sapa/home/app-update-version")
    suspend fun appVersion(
        @Header("Authorization") authKey: String
    ): AppVersionResponse

    @GET("/v1/petping/shop/goods/recommendation")
    suspend fun getShopItemsList(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<ShopItemResponse>

    @POST("/v1/shop/petping/members/{memberId}")
    suspend fun shopSignUp(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String
    ): CommonResponse<ShopSignUpResponse>

    /**
     * AccessToken 만료시간 조회
     *
     * @return
     */
    @GET("/v1/petping/authtoken-expiretime")
    suspend fun getAccessTokenTime(
        @Header("Authorization") authKey: String
    ): CommonResponse<AccessTokenResponse>

    @GET("/v1/shop/petping/rewards")
    suspend fun getPingAmount(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<ShopReward>

    /**
     * 고도몰 로그인 API 직접 요청
     *
     * @param body
     * @return
     */
    @POST("https://shop.petping.com/account/login")
    suspend fun shopLogin(
        @Header("X-Requested-With") header: String,
        @Query("__gd5_work_preview") flag: String,
        @Query("tplMobileSkin") mode: String,
        @Body body: RequestBody
    ): CommonResponse<ShopLoginResponse>

    /**
     * 산책가능한 펫 목록
     */
    @Streaming
    @GET
    suspend fun getDownLoadFileUrl(@Url url: String): ResponseBody

    /**
     * 산책가능한 펫 목록
     */
    @GET("/v1/petping/pets/walkable")
    suspend fun walkablePetList(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<WalkablePet>

    /**
     * 산책시작
     */
    @POST("/v1/petping/walks")
    suspend fun startWalk(
        @Header("Authorization") authKey: String,
        @Body body: WalkStartRequest
    ): CommonResponse<WalkStart>

    /**
     * 산책종료
     */
    @PUT("/v1/petping/walks/{walkId}/finish")
    suspend fun finishWalk(
        @Header("Authorization") authKey: String,
        @Path("walkId") walkId: Int,
        @Body body: WalkFinishRequest
    ): CommonResponse<WalkFinish>

    /**
     * 산책종료 산책기록
     */
    @Multipart
    @POST("/v1/petping/walks/{walkId}/end/comment")
    suspend fun walkFinishRecord(
        @Header("Authorization") authKey: String,
        @Path("walkId") walkId: Int,
        @Part review: MultipartBody.Part,
        @Part file: List<MultipartBody.Part>
    ): CommonResponse<Any>

    @POST("/v1/petping/walks/{walkId}/marking")
    suspend fun registerMyMarking(
        @Header("Authorization") authKey: String,
        @Path("walkId") walkId: Int,
        @Body requestBody: MyMarkingPoi
    ): CommonResponse<Any>

    /**
     * 장소 리스트 가져오기
     */
    @GET("/v1/petping/walks/place")
    suspend fun placePoiList(
        @Header("Authorization") authKey: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String
    ): CommonResponse<PlacePoi>

    /**
     * 마킹 리스트 가져오기
     */
    @GET("/v1/petping/walks/marking-pois")
    suspend fun markingPoiList(
        @Header("Authorization") authKey: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("zoomLevel") zoomLevel: Int
    ): CommonListResponse<MarkingPoi>

    /**
     * 마킹 상세 가져오기
     */
    @GET("/v1/petping/walks/marking/{markingId}")
    suspend fun markingDetail(
        @Header("Authorization") authKey: String,
        @Path("markingId") markingId: Int
    ): CommonResponse<MarkingDetail>

    /**
     * 플레이스 상세 가져오기
     */
    @GET("/v1/petping/walks/place/{placeId}")
    suspend fun placeDetail(
        @Header("Authorization") authKey: String,
        @Path("placeId") placeId: Int
    ): CommonResponse<PlaceDetail>

    /**
     * 상세 핑 데이터를 가져온다.
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/rewards/{member_id}/detail-pings")
    suspend fun getDetailPings(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String
    ): CommonResponse<DetailPings>

    /**
     * 사용 가능 핑 데이터를 가져온다.
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/rewards/{member_id}/available-pings")
    suspend fun getAvailablePings(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String
    ): CommonResponse<AvailablePings>


    @GET("/v1/petping/rewards/{member_id}/history/saving-use-pings")
    suspend fun getSavingPings(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String,
        @Query("pageNo") pageNo: Number,
        @Query("historyType") type: Number
    ): CommonResponse<SavingPings>

    @GET("/v1/petping/rewards/{member_id}/history/expiration-pings")
    suspend fun getExpiredPings(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String,
        @Query("pageNo") pageNo: Number
    ): CommonResponse<ExpiredPings>

    @GET("/v1/petping/rewards/{member_id}/history/saving-use-pings")
    suspend fun getUsingPings(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String,
        @Query("pageNo") pageNo: Number,
        @Query("historyType") type: Number
    ): CommonResponse<UsingPings>

    @GET("/v1/petping/rewards/{member_id}/ongoing-missions")
    suspend fun getOngoingMissions(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String,
        @Query("pageNo") pageNo: Number
    ): OngoingMissionsResponse

    @GET("/v1/petping/rewards/{member_id}/completion-missions")
    suspend fun getCompletionMissions(
        @Header("Authorization") authKey: String,
        @Path("member_id") memberId: String,
        @Query("pageNo") pageNo: Number
    ): CompletionMissionResponse

    @GET("/v1/petping/pets")
    suspend fun getPetList(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<PetsData>

    @Multipart
    @POST("/v1/petping/rewards/file-upload")
    suspend fun uploadMissionFiles(
        @Header("Authorization") authKey: String,
        @Part memberId: MultipartBody.Part,
        @Part missionId: MultipartBody.Part,
        @Part file: List<MultipartBody.Part>
    ): CommonResponse<Any>

    /**
     * 대표 미션 반려견을 조회한다.
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/my-pages/representativeMissionPet")
    suspend fun getRepresentativePet(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<List<RepresentativePet>>

    /**
     * 펫보험 미션 반려견을 조회한다.
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/my-pages/petInsuranceMissionPet")
    suspend fun getPetInsuranceMissionPet(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonListResponse<PetInsuranceMissionPet>

    /**
     * 대표 미션 반려견 설정 및 변경을 위한 목록 조회한다.
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/my-pages/candidateRepreMisPets")
    suspend fun getCandidateRepreMisPets(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonListResponse<InsurancePet>

    /**
     * 대표 미션 반려견 설정 및 변경한다.
     *
     * @param authKey
     * @param body
     * @return
     */
    @POST("/v1/petping/my-pages/representativeMissionPet")
    suspend fun setRepresentativeMissionPet(
        @Header("Authorization") authKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/my-pages")
    suspend fun getMyPageInfo(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<MyPageData>

    @GET("/v1/petping/my-pages/notices")
    suspend fun getNoticeList(
        @Header("Authorization") authKey: String
    ): CommonResponse<List<NoticeResponseData>>

    //[더보기/반려견] 회원 정보 조회
    @GET("/v1/petping/members/{memberId}")
    suspend fun getMemberInfo(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String
    ): CommonResponse<MemberInfo>

    @POST("/v1/petping/auth-email/send")
    suspend fun sendAuthEmail(
        @Header("Authorization") authKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/my-pages/faqs")
    suspend fun getFaqList(
        @Header("Authorization") authKey: String
    ): CommonResponse<List<NoticeResponseData>>

    @GET("/v1/petping/my-pages/personalInquirys")
    suspend fun getInquirys(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonListResponse<InquiryData>

    @GET("/v1/petping/appInfos")
    suspend fun getAppInfo(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<AppInfo>

    @GET("/v1/petping/my-pages/notification-receiving-statuses/")
    suspend fun getNotificationStatus(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<FCMNotification>

    //마케팅 수신 알람 수정
    @PUT("/v1/petping/members/{memberId}")
    suspend fun setPushMarketingInfo(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @PUT("/v1/petping/my-pages/notification-receiving-statuses/{type}")
    suspend fun setNotificationStatus(
        @Header("Authorization") authKey: String,
        @Path("type") type: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/my-pages/applicable-welcome-kit")
    suspend fun getWelcomeKitApply(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonResponse<WelcomeKit>

    @GET("/v1/petping/pets/{petId}/profiles")
    suspend fun getWalkHistoryProfile(
        @Header("Authorization") authKey: String,
        @Path("petId") petId: Int,
        @Query("memberId") memberId: String,
        @Query("viewMode") viewMode: String
    ): CommonResponse<PetProfileData>

    @GET("/v1/petping/pets/{petId}/walk-record")
    suspend fun getWalkingRecords(
        @Header("Authorization") authKey: String,
        @Path("petId") petId: Int,
        @Query("memberId") memberId: String,
        @Query("startIndex") startIndex: Int,
        @Query("viewMode") viewMode: String
    ): CommonResponse<WalkRecordingResponseData>

    @DELETE("/v1/petping/walks/{walkId}/records")
    suspend fun deleteWalkRecord(
        @Header("Authorization") authKey: String,
        @Path("walkId") id: Int
    ): CommonResponse<Any>

    @GET(" /v1/petping/pets/{petId}/walking-stats")
    suspend fun getWalkStats(
        @Header("Authorization") authKey: String,
        @Path("petId") petId: Int
    ): CommonResponse<WalkStatsData>

     @GET(" /v2/petping/walks/audio-guide")
    suspend fun walkAudioGuide(
         @Header("Authorization") authKey: String,
         @Query("pageNo") pageNo: Int
    ): CommonResponse<WalkAudioGuide>

    // 이메일 중복
    @GET("/v1/sapa/accounts/email/duplication-chk")
    suspend fun requestHasDuplicateEmail(
        @Header("Authorization") sapaAuthKey: String,
        @Query("email") email: String
    ): DuplicationResponse

    //회원 정보 수정
    @PUT("/v1/petping/members/{memberId}")
    suspend fun setMemberInfo(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    // 로그인
    @POST("/v1/sapa/accounts/login")
    suspend fun requestSignIn(
        @Header("Authorization") sapaAuthKey: String,
        @Body body: RequestBody
    ): CommonResponse<SignInResponseData>

    @GET("/v1/sapa/accounts/policies")
    suspend fun getPolicies(
        @Header("Authorization") sapaAuthKey: String
    ): CommonListResponse<PolicyData>

    @POST("/v1/sapa/members-account")
    suspend fun requestSignUpSapa(
        @Header("Authorization") sapaAuthKey: String,
        @Header("appVersion") appVersion: String,
        @Header("osVersion") osVersion: String,
        @Header("deviceInfo") deviceInfo: String,
        @Header("mobileCarrierInfo") mobileCarrierInfo: String,
        @Header("deviceId") deviceId: String,
        @Body body: RequestBody
    ): CommonResponse<SignUpResponseData>

    @POST("/v1/sapa/accounts/password/find")
    suspend fun findPassword(
        @Header("Authorization") sapaAuthKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    //본인 인증된 데이터를 서버에 전달
    @POST("/v1/petping/validation")
    suspend fun setMemberValidation(
        @Header("Authorization") authKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/my-pages/leaveReasons")
    suspend fun getLeaveType(
        @Header("Authorization") authKey: String
    ): CommonListResponse<LeaveType>

    @POST("/v1/petping/members/{memberId}")
    suspend fun withdrawalId(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/members/{memberId}/location-information")
    suspend fun getLocationHistoryList(
        @Header("Authorization") authKey: String,
        @Path("memberId") memberId: String,
        @Query("startIndex") startIndex:Int
    ): CommonResponse<LocationHistoryResponse>

    /**
     * 펫 정보를 수정한다.
     *
     * @param authKey
     * @param petId
     * @param body
     * @return
     */
    @PUT("/v1/petping/pets/{petId}")
    suspend fun modifyPetRegisterNumber(
        @Header("Authorization") authKey: String,
        @Path("petId") petId: Int,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/my-pages/petInsurJoins")
    suspend fun getPetInsurJoins(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonListResponse<PetInsurJoinsData>

    @GET("/v1/petping/my-pages/candidateInsurConnectPets")
    suspend fun getCandidateInsurConnectPets(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): CommonListResponse<InsurancePet>

    /**
     * 펫보험 가입 가능 여부 체크
     *
     * @param authKey
     * @param memberId
     * @return
     */
    @GET("/v1/petping/my-pages/petInsurAvailableCheck")
    suspend fun getPetInsuranceCheck(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String
    ): PetInsuranceCheckResponse

    @POST("/v1/petping/my-pages/petInsurConnection")
    suspend fun connectPetInsurance(
        @Header("Authorization") authKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @GET("/v1/petping/validation-ci")
    suspend fun checkValidationCI(
        @Header("Authorization") authKey: String,
        @Query("value") value: String
    ): CommonResponse<Any>

    /**
     * 펫 프로필을 조회한다.
     *
     * @param authKey
     * @param petId
     * @param memberId
     * @return
     */
    @GET("/v1/petping/pet-profiles")
    suspend fun getPetProfile(
        @Header("Authorization") authKey: String,
        @Query("petId") petId: Int,
        @Query("memberId") memberId: String
    ): CommonResponse<PetProfileData>

    /**
     * 가족 코드 등록을 해제한다.
     *
     * @param authKey
     * @param targetProfileId
     * @param unlinkBody
     * @return
     */
    @PUT("/v1/petping/pet-profiles/{targetProfileId}/family-disconnect")
    suspend fun unlinkFamily(
        @Header("Authorization") authKey: String,
        @Path("targetProfileId") targetProfileId: Int,
        @Body body: RequestBody
    ): CommonResponse<Any>

    @PUT("/v1/petping/pet-characters/{petCharId}")
    suspend fun modifyPetCharacter(
        @Header("Authorization") authKey: String,
        @Path("petCharId") petCharId: Int,
        @Body body: RequestBody
    ): CommonResponse<PetCharacterResponseData>

    @Multipart
    @PUT("/v1/petping/pet-profiles/{profileId}")
    suspend fun modifyPetProfile(
        @Header("Authorization") authKey: String,
        @Path("profileId") profileId: Int,
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>
    ): CommonResponse<ModifyProfileResponseData>

    @Multipart
    @PUT("/v1/petping/pet-profiles/{petId}")
    suspend fun modifyPetProfileWithFile(
        @Header("Authorization") authKey: String,
        @Path("petId") petId: Int,
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photoFile: MultipartBody.Part
    ): CommonResponse<ModifyProfileResponseData>

    @GET("/v1/petping/breeds")
    suspend fun getBreedList(
        @Header("Authorization") authKey: String
    ): CommonResponse<Breed>

    @DELETE("/v1/petping/pet-profiles/{profileId}")
    suspend fun deletePetProfile(
        @Header("Authorization") authKey: String,
        @Path("profileId") profileId: Int
    ): CommonResponse<Any>

    @GET("/v1/petping/home/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authKey: String,
        @Header("appVersion") appVersion: String,
        @Header("osVersion") osVersion: String,
        @Header("deviceInfo") deviceInfo: String,
        @Header("mobileCarrierInfo") mobileCarrierInfo: String,
        @Header("deviceId") deviceId: String,
        @Query("lat") lat: String,
        @Query("lng") lng: String
    ): DashboardResponse

    @GET("/v1/petping/home/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authKey: String,
        @Header("appVersion") appVersion: String,
        @Header("osVersion") osVersion: String,
        @Header("deviceInfo") deviceInfo: String,
        @Header("mobileCarrierInfo") mobileCarrierInfo: String,
        @Header("deviceId") deviceId: String,
        @Query("profileId") profileId: Number,
        @Query("lat") lat: String,
        @Query("lng") lng: String
    ): DashboardResponse

    @GET("/v1/petping/home/contents")
    suspend fun getPingTip(
        @Header("Authorization")
        authKey: String
    ): ContentsResponse

    @GET("/v1/petping/profiles")
    suspend fun getFamilyProfile(
        @Header("Authorization") authKey: String,
        @Query("memberId") memberId: String,
        @Query("familyCode") familyCode: String
    ): CommonResponse<FamilyProfile>

    @POST("/v1/petping/profiles")
    suspend fun registerFamilyProfile(
        @Header("Authorization") authKey: String,
        @Body body: RequestBody
    ): CommonResponse<Any>

    //프로필 펫사진 업로드
    @Multipart
    @POST("/v1/petping/pets")
    suspend fun requestMakeProfile(
        @Header("Authorization") authKey: String,
        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photoFile: MultipartBody.Part?
    ): ProfileResponse

//    @Multipart
//    @POST("/v1/petping/pets")
//    suspend fun requestMakeProfile(
//        @Header("Authorization") authKey: String,
//        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>
//    ): ProfileResponse
//
//    @Multipart
//    @POST("/v1/petping/pets")
//    fun requestMakeProfileWithFile(
//        @Header("Authorization") authKey: String,
//        @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>,
//        @Part photoFile: MultipartBody.Part
//    ): ProfileResponse

    @GET("/v1/petping/home/pingzone-meet-pets")
    suspend fun getPingZoneFriends(
        @Header("Authorization") authKey: String,
        @Query("petId") petId: Int
    ): PingZoneResponse

    @GET("/v1/petping/my-pages/personalInquiryTypes")
    suspend fun getInquiryType(
        @Header("Authorization") authKey: String
    ): CommonListResponse<InquiryType>

    @Multipart
    @POST("/v1/petping/my-pages/personalInquirys")
    suspend fun uploadPersonalInquirys(
        @Header("Authorization") authKey: String,
        @Part memberId: MultipartBody.Part,
        @Part inquiryType: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part appVersion: MultipartBody.Part,
        @Part osVersion: MultipartBody.Part,
        @Part deviceInfo: MultipartBody.Part,
        @Part mobileCarrierInfo: MultipartBody.Part,
        @Part deviceId: MultipartBody.Part,
        @Part file: List<MultipartBody.Part>
    ): CommonResponse<Any>

    /**
     * 앱 버전을 가져온다.(토큰이 없는 경우)
     *
     */
    @GET("/v1/sapa/home/app-update-version")
    suspend fun getAppVersion(
        @Header("Authorization") authKey: String
    ): AppVersionResponse

    /**
     * [가입/로그인] FCM Token 저장
     *
     * @param authKey
     * @param requestBody
     * @return
     */
    @POST("/v1/petping/fcm-tokens")
    suspend fun registFcmToken(
        @Header("Authorization") authKey: String,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>
}
