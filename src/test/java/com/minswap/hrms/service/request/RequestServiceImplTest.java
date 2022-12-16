package com.minswap.hrms.service.request;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import java.util.*;

import static org.mockito.Mockito.*;

public class RequestServiceImplTest {
    @Mock
    RequestRepository requestRepository;
    @Mock
    LeaveBudgetRepository leaveBudgetRepository;
    @Mock
    OTBudgetRepository otBudgetRepository;
    @Mock
    EntityManager entityManager;
    @Mock
    RequestTypeRepository requestTypeRepository;
    @Mock
    DeviceTypeRepository deviceTypeRepository;
    @Mock
    EvidenceRepository evidenceRepository;
    @Mock
    OfficeTimeRepository officeTimeRepository;
    @Mock
    TimeCheckRepository timeCheckRepository;
    @Mock
    PersonRepository personRepository;
    @Mock
    NotificationRepository notificationRepository;
    //Field httpStatus of type HttpStatus - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    Set<Integer> LEAVE_REQUEST_TYPE;
    @Mock
    Logger log;
    @InjectMocks
    RequestServiceImpl requestServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetQueryForRequestList() throws Exception {
        List<RequestDto> result = requestServiceImpl.getQueryForRequestList("type", Long.valueOf(1), Long.valueOf(1), Boolean.TRUE, Integer.valueOf(0), Integer.valueOf(0), "search", "createDateFrom", "createDateTo", Long.valueOf(1), "status", "sort", "dir");
        Assert.assertEquals(List.of(new RequestDto(Long.valueOf(1), "rollNumber", "personName", Long.valueOf(1), "requestTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), List.of("String"), "reason", "status", "receiver", Long.valueOf(1), "deviceTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0, Integer.valueOf(0), Long.valueOf(1), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0d, 0)), result);
    }

    @Test
    public void testGetRequestByPermission() throws Exception {
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> result = requestServiceImpl.getRequestByPermission("type", Long.valueOf(1), Long.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), "search", "createDateFrom", "createDateTo", Long.valueOf(1), "status", "sort", "dir");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetAllRequest() throws Exception {
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> result = requestServiceImpl.getAllRequest(Integer.valueOf(0), Integer.valueOf(0), "search", "createDateFrom", "createDateTo", Long.valueOf(1), "status", "sort", "dir");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetSubordinateRequest() throws Exception {
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> result = requestServiceImpl.getSubordinateRequest(Long.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), "search", "createDateFrom", "createDateTo", Long.valueOf(1), "status", "sort", "dir");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetMyRequest() throws Exception {
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> result = requestServiceImpl.getMyRequest(Long.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), "search", "createDateFrom", "createDateTo", Long.valueOf(1), "status", "sort", "dir");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetEmployeeRequestDetail() throws Exception {
        when(requestRepository.getEmployeeRequestDetail(anyLong())).thenReturn(new RequestDto(Long.valueOf(1), "rollNumber", "personName", Long.valueOf(1), "requestTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), List.of("String"), "reason", "status", "receiver", Long.valueOf(1), "deviceTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0, Integer.valueOf(0), Long.valueOf(1), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0d, 0));
        when(requestRepository.getStartAndEndTimeByRequestId(anyLong())).thenReturn(new DateDto(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), null));
        when(requestRepository.getPersonIdByRequestId(anyLong())).thenReturn(Long.valueOf(1));
        when(requestRepository.getMaximumTimeToRollback(anyLong())).thenReturn(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime());
        when(requestRepository.isAssignedOrNot(anyLong())).thenReturn(Integer.valueOf(0));
        when(leaveBudgetRepository.getLeaveBudget(anyLong(), any(), anyLong())).thenReturn(new LeaveBudgetDto(null, null, null, null, Double.valueOf(0)));
        when(otBudgetRepository.getOTBudgetByPersonId(anyLong(), any(), anyInt())).thenReturn(new OTBudgetDto(0d, 0d, 0d, 0d));
        when(requestTypeRepository.getRequestTypeByRequestId(anyLong())).thenReturn(Integer.valueOf(0));
        when(evidenceRepository.getListImageByRequest(anyLong())).thenReturn(List.of("String"));
        when(personRepository.getRollNumberByPersonId(anyLong())).thenReturn("getRollNumberByPersonIdResponse");
        when(personRepository.getManagerIdByPersonId(anyLong())).thenReturn(Long.valueOf(1));
        when(personRepository.getListRoleIdByPersonId(anyLong())).thenReturn(List.of(Long.valueOf(1)));

        ResponseEntity<BaseResponse<RequestResponse, Void>> result = requestServiceImpl.getEmployeeRequestDetail(Long.valueOf(1), Long.valueOf(3));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testCreateRequest() throws Exception {
        when(requestRepository.getLastRequestId()).thenReturn(Integer.valueOf(0));
        when(requestRepository.getListRequestApprovedByDate(anyLong(), any(), any(), anyString())).thenReturn(List.of(new DateDto(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime())));
        when(requestRepository.getLeaveRequestTimeAlreadyInAnotherLeaveRequest(anyLong(), any(), any(), anyString())).thenReturn(new ArrayList<>());
        when(leaveBudgetRepository.getLeaveBudget(anyLong(), any(), anyLong())).thenReturn(new LeaveBudgetDto(Long.valueOf(100), null, 100.0, 0.0, 100.0));
        when(otBudgetRepository.getOTBudgetByPersonId(anyLong(), any(), anyInt())).thenReturn(new OTBudgetDto(100, 0, 40, 100));
        when(requestTypeRepository.getAllRequestTypeId()).thenReturn(List.of(Long.valueOf(1)));
        when(deviceTypeRepository.getAllDeviceTypeId()).thenReturn(List.of(Long.valueOf(1)));
        when(officeTimeRepository.getOfficeTime()).thenReturn(new OfficeTimeDto("08:30:00", "17:30:00", "12:30:00", "13:30:00"));
        when(personRepository.getManagerIdByPersonId(anyLong())).thenReturn(Long.valueOf(1));

        ResponseEntity<BaseResponse<Void, Void>> result = requestServiceImpl.createRequest(new CreateRequest(Long.valueOf(1), Long.valueOf(1), "2022-12-28 08:04:00", "2022-12-28 17:07:00", "reason", List.of("String")), Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testEditRequest() throws Exception {
        when(requestRepository.updateNormalRequest(anyLong(), any(), any(), anyString())).thenReturn(Integer.valueOf(100));
        when(requestRepository.getStatusOfRequestById(anyLong())).thenReturn("Pending");
        when(requestRepository.isRequestIdValid(anyLong())).thenReturn(Integer.valueOf(100));
        when(requestRepository.getPersonIdByRequestId(anyLong())).thenReturn(Long.valueOf(100));
        when(requestRepository.getListRequestApprovedByDate(anyLong(), any(), any(), anyString())).thenReturn(List.of(new DateDto(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime())));
        when(requestRepository.getLeaveRequestTimeAlreadyInAnotherLeaveRequest(anyLong(), any(), any(), anyString())).thenReturn(new ArrayList<>());
        when(leaveBudgetRepository.getLeaveBudget(anyLong(), any(), anyLong())).thenReturn(new LeaveBudgetDto(null, null, null, null, Double.valueOf(0)));
        when(otBudgetRepository.getOTBudgetByPersonId(anyLong(), any(), anyInt())).thenReturn(new OTBudgetDto(0d, 0d, 0d, 0d));
        when(requestTypeRepository.getRequestTypeByRequestId(anyLong())).thenReturn(Integer.valueOf(0));
        when(deviceTypeRepository.getAllDeviceTypeId()).thenReturn(List.of(Long.valueOf(100)));
        when(evidenceRepository.deleteImageByRequestId(anyLong())).thenReturn(Integer.valueOf(0));
        when(officeTimeRepository.getOfficeTime()).thenReturn(new OfficeTimeDto("08:30:00", "17:30:00", "12:30:00", "13:30:00"));

        ResponseEntity<BaseResponse<Void, Void>> result = requestServiceImpl.editRequest(new EditRequest(Long.valueOf(1), "2022-12-28 08:04:00", "2022-12-28 17:07:00", "reason", List.of("String")), Long.valueOf(100), Long.valueOf(100));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testUpdateRequestStatus() throws Exception {
        when(requestRepository.getEmployeeRequestDetail(anyLong())).thenReturn(new RequestDto(Long.valueOf(1), "rollNumber", "personName", Long.valueOf(1), "requestTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), List.of("String"), "reason", "status", "receiver", Long.valueOf(1), "deviceTypeName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0, Integer.valueOf(0), Long.valueOf(1), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), 0d, 0d, 0));
        when(requestRepository.updateStatusRequest(anyString(), anyLong(), any())).thenReturn(Integer.valueOf(0));
        when(requestRepository.getStatusOfRequestById(anyLong())).thenReturn("Pending");
        when(requestRepository.getStartAndEndTimeByRequestId(anyLong())).thenReturn(new DateDto(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime()));
        when(requestRepository.isRequestIdValid(anyLong())).thenReturn(Integer.valueOf(0));
        when(requestRepository.getPersonIdByRequestId(anyLong())).thenReturn(Long.valueOf(1));
        when(requestRepository.getListRequestApprovedByDate(anyLong(), any(), any(), anyString())).thenReturn(List.of(new DateDto(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime(), new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime())));
        when(requestRepository.getMaximumTimeToRollback(anyLong())).thenReturn(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime());
        when(requestRepository.updateMaximumTimeToRollback(anyLong(), any())).thenReturn(Integer.valueOf(0));
        when(requestRepository.getLeaveRequestTimeAlreadyInAnotherLeaveRequest(anyLong(), any(), any(), anyString())).thenReturn(List.of(Long.valueOf(1)));
        when(leaveBudgetRepository.getLeaveBudget(anyLong(), any(), anyLong())).thenReturn(new LeaveBudgetDto(Long.valueOf(100), null, 100.0, 0.0, 100.0));
        when(leaveBudgetRepository.updateLeaveBudget(anyLong(), anyDouble(), anyDouble(), any(), anyLong())).thenReturn(Integer.valueOf(0));
        when(otBudgetRepository.getOTBudgetByPersonId(anyLong(), any(), anyInt())).thenReturn(new OTBudgetDto(100, 0, 40, 100));
        when(otBudgetRepository.updateOTBudgetOfMonth(anyLong(), any(), anyInt(), anyDouble(), anyDouble())).thenReturn(Integer.valueOf(0));
        when(otBudgetRepository.updateOTBudgetOfYear(anyLong(), any(), anyDouble())).thenReturn(Integer.valueOf(0));
        when(requestTypeRepository.getRequestTypeByRequestId(anyLong())).thenReturn(Integer.valueOf(0));
        when(requestTypeRepository.getRequestTypeNameByRequestId(anyLong())).thenReturn("getRequestTypeNameByRequestIdResponse");
        when(officeTimeRepository.getOfficeTime()).thenReturn(new OfficeTimeDto("08:30:00", "17:30:00", "12:30:00", "13:30:00"));
        when(timeCheckRepository.getTimeInOfPersonByDay(anyLong(), anyInt(), anyInt())).thenReturn(new GregorianCalendar(2022, Calendar.DECEMBER, 8, 13, 47).getTime());
        when(timeCheckRepository.updateTimeCheckOfEmployee(anyLong(), anyDouble(), anyDouble(), any(), any(), anyDouble(), anyDouble(), anyInt())).thenReturn(Integer.valueOf(0));
        when(timeCheckRepository.getOTTimeByDay(anyInt(), anyLong())).thenReturn(Double.valueOf(0));
        when(timeCheckRepository.updateOTTime(anyInt(), anyLong(), anyDouble(), anyInt())).thenReturn(Integer.valueOf(0));
        when(timeCheckRepository.deleteTimeCheckByDate(anyInt(), anyInt(), anyInt())).thenReturn(Integer.valueOf(0));
        when(personRepository.getManagerIdByPersonId(anyLong())).thenReturn(Long.valueOf(3));
        when(personRepository.getListRoleIdByPersonId(anyLong())).thenReturn(List.of(Long.valueOf(2)));

        ResponseEntity<BaseResponse<Void, Void>> result = requestServiceImpl.updateRequestStatus("status", Long.valueOf(1), Long.valueOf(3));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testCancelRequest() throws Exception {
        when(requestRepository.updateStatusRequest(anyString(), anyLong(), any())).thenReturn(Integer.valueOf(1));
        when(requestRepository.getStatusOfRequestById(anyLong())).thenReturn("Pending");
        when(requestRepository.isRequestIdValid(anyLong())).thenReturn(Integer.valueOf(1));
        when(requestRepository.getPersonIdByRequestId(anyLong())).thenReturn(Long.valueOf(1));

        ResponseEntity<BaseResponse<Void, Void>> result = requestServiceImpl.cancelRequest(Long.valueOf(1), Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
