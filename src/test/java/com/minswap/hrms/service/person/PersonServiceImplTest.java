package com.minswap.hrms.service.person;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Role;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.request.*;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.ListRolesResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.department.DepartmentService;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.service.position.PositionService;
import com.minswap.hrms.service.rank.RankService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.*;

public class PersonServiceImplTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    PersonRoleRepository personRoleRepository;
    @Mock
    EmailSenderService emailSenderService;
    @Mock
    DepartmentService departmentService;
    @Mock
    PositionService positionService;
    @Mock
    RankService rankService;
    @Mock
    LeaveBudgetRepository leaveBudgetRepository;
    @Mock
    OTBudgetRepository otBudgetRepository;
    @InjectMocks
    PersonServiceImpl personServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetMasterDataAllManager() throws Exception {
        when(personRepository.getMasterDataManagerByDepartment(anyLong(), anyString(), anyLong())).thenReturn(List.of(new Person(Long.valueOf(1), "fullName", "address", "citizenIdentification", "phoneNumber", "email", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), 0, "rollNumber", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), "status", Long.valueOf(1), Double.valueOf(0), Double.valueOf(0), "avatarImg", Double.valueOf(0), "pinCode")));

        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> result = personServiceImpl.getMasterDataAllManager(Long.valueOf(1), "search");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetDetailEmployee() throws Exception {
        when(personRepository.getDetailEmployee(anyString())).thenReturn(new EmployeeDetailDto(Long.valueOf(1), "fullName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), 0, "phoneNumber", "citizenIdentification", "address", "rollNumber", "email", Long.valueOf(1), "departmentName", Long.valueOf(1), "positionName", Long.valueOf(1), "rankingName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), "status", Long.valueOf(1), "managerName", "avatarImg", Double.valueOf(0), Double.valueOf(0), Integer.valueOf(0)));

        ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> result = personServiceImpl.getDetailEmployee("rollNumber");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testUpdateStatusEmployee() throws Exception {
        when(personRepository.updateStatusEmployee(anyString(), anyString())).thenReturn(Integer.valueOf(0));

        ResponseEntity<BaseResponse<Void, Void>> result = personServiceImpl.updateStatusEmployee(new ChangeStatusEmployeeRequest("gmail"), "rollNumber");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testIsValidHeaderTemplate() throws Exception {
        boolean result = personServiceImpl.isValidHeaderTemplate(null);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckManagerByDepartmentValid() throws Exception {
        when(personRoleRepository.findByPersonIdAndAndRoleId(anyLong(), anyLong())).thenReturn(null);

        boolean result = personServiceImpl.checkManagerByDepartmentValid(Long.valueOf(1), Long.valueOf(1));
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckGenderValid() throws Exception {
        boolean result = personServiceImpl.checkGenderValid(Integer.valueOf(0));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testCheckIsManagerValid() throws Exception {
        boolean result = personServiceImpl.checkIsManagerValid(Integer.valueOf(0));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testCheckPhoneValid() throws Exception {
        boolean result = personServiceImpl.checkPhoneValid("phone");
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckCCCDValid() throws Exception {
        boolean result = personServiceImpl.checkCCCDValid("cccd");
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckSalaryValid() throws Exception {
        boolean result = personServiceImpl.checkSalaryValid(Double.valueOf(0));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testImportExcel() throws Exception {
        when(personRepository.getDetailEmployee(anyString())).thenReturn(new EmployeeDetailDto(Long.valueOf(1), "fullName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), 0, "phoneNumber", "citizenIdentification", "address", "rollNumber", "email", Long.valueOf(1), "departmentName", Long.valueOf(1), "positionName", Long.valueOf(1), "rankingName", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), "status", Long.valueOf(1), "managerName", "avatarImg", Double.valueOf(0), Double.valueOf(0), Integer.valueOf(0)));
        when(personRepository.getUserByCitizenIdentification(anyString())).thenReturn(Integer.valueOf(0));
        when(personRoleRepository.findByPersonIdAndAndRoleId(anyLong(), anyLong())).thenReturn(null);
        when(departmentService.checkDepartmentExist(anyLong())).thenReturn(true);
        when(positionService.checkPositionByDepartment(anyLong(), anyLong())).thenReturn(true);
        when(rankService.checkRankExist(anyLong())).thenReturn(true);
        ResponseEntity<BaseResponse<HttpStatus, Void>> result = personServiceImpl.importExcel(null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testGetPersonInforByEmail() throws Exception {
        Person person = new Person(Long.valueOf(1), "fullName", "address", "citizenIdentification", "phoneNumber", "email", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), 0, "rollNumber", new GregorianCalendar(2022, Calendar.DECEMBER, 8, 19, 10).getTime(), "status", Long.valueOf(1), Double.valueOf(0), Double.valueOf(0), "avatarImg", Double.valueOf(0), "pinCode");
        when(personRepository.findPersonByEmail(anyString())).thenReturn(Optional.of(person));
        Person result = personServiceImpl.getPersonInforByEmail("email");
        Assert.assertEquals(person, result);
    }

    @Test
    public void testGetRoles() throws Exception {
        ResponseEntity<BaseResponse<ListRolesResponse, Void>> result = personServiceImpl.getRoles(new UserPrincipal("email", "name", List.of(new Role(Long.valueOf(1), "roleName"))));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testExportEmployee() throws Exception {
        when(personRepository.getSearchListPerson(anyString(), anyString(), anyLong(), anyString(), anyLong(), anyLong(), anyString(), any())).thenReturn(null);
        List<EmployeeListDto> result = personServiceImpl.exportEmployee("fullName", "email", Long.valueOf(1), "rollNumber", Long.valueOf(1));
        Assert.assertEquals(null, result);
    }

}

