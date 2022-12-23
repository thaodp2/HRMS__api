package com.minswap.hrms.service.payroll;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PayrollRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.response.dto.PayrollDisplayDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

import static com.minswap.hrms.constants.ErrorCode.SEND_FAIL;
import static com.minswap.hrms.constants.ErrorCode.UNAUTHORIZE;

@Service
public class PayrollServiceImpl implements PayrollService{
    @Autowired
    PayrollRepository  payrollRepository;

    HttpStatus httpStatus;
    @Autowired
    PersonRepository personRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PersonService personService;

    @Override
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getDetailPayroll(int month, int year, Long personId, String currentCode) {

        Optional<Person> person = personRepository.findById(personId);
        if(!person.isPresent()){
            throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
        }
        String pinCode = person.get().getPinCode() == null ? "" : person.get().getPinCode();
        if (!pinCode.equalsIgnoreCase(currentCode)) {
            throw new BaseException(ErrorCode.HAVE_NO_PERMISSION);
        }
        Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
        if(!salaryFromDB.isPresent()){
            throw new BaseException(ErrorCode.PAYSLIP_NOT_EXIST);
        }
        PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
        Salary salary = salaryFromDB.get();
        payrollDisplayDto.setPersonId(personId);
        payrollDisplayDto.setPersonName(person.get().getFullName());
        payrollDisplayDto.setRollNumber(person.get().getRollNumber());
        payrollDisplayDto.setActualWork(salary.getActualWork());
        payrollDisplayDto.setTotalWork(salary.getTotalWork());
        payrollDisplayDto.setBasicSalary(String.format("%,.0f",Double.valueOf(salary.getBasicSalary())));
        payrollDisplayDto.setOtSalary(String.format("%,.0f",Double.valueOf(salary.getOtSalary())));
        payrollDisplayDto.setFineAmount(String.format("%,.0f",Double.valueOf(salary.getFineAmount())));
        payrollDisplayDto.setSalaryBonus(String.format("%,.0f",Double.valueOf(salary.getBonus())));
        payrollDisplayDto.setTax(String.format("%,.0f",Double.valueOf(salary.getTax())));
        payrollDisplayDto.setSocialInsurance(String.format("%,.0f",Double.valueOf(salary.getSocialInsurance())));
        payrollDisplayDto.setActuallyReceived(String.format("%,.0f",Double.valueOf(salary.getActuallyReceived())));

        PayrollResponse payrollResponse = new PayrollResponse(payrollDisplayDto);
        ResponseEntity<BaseResponse<PayrollResponse, Void>> responseEntity = BaseResponse.ofSucceeded(payrollResponse);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(UserPrincipal userPrincipal, int month, int year, String currentCode) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
            Optional<Person> person = personRepository.findById(personId);
            if(!person.isPresent()){
                throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
            }
            String pinCode = person.get().getPinCode() == null ? "" : person.get().getPinCode();
            if (!pinCode.equalsIgnoreCase(currentCode)) {
                throw new BaseException(ErrorCode.HAVE_NO_PERMISSION);
            }
            Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
            if(!salaryFromDB.isPresent()){
                throw new BaseException(ErrorCode.PAYSLIP_NOT_EXIST);
            }

            PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
            Salary salary = salaryFromDB.get();
            payrollDisplayDto.setPersonId(personId);
            payrollDisplayDto.setPersonName(person.get().getFullName());
            payrollDisplayDto.setRollNumber(person.get().getRollNumber());
            payrollDisplayDto.setActualWork(salary.getActualWork());
            payrollDisplayDto.setTotalWork(salary.getTotalWork());
            payrollDisplayDto.setBasicSalary(String.format("%,.0f",Double.valueOf(salary.getBasicSalary())));
            payrollDisplayDto.setOtSalary(String.format("%,.0f",Double.valueOf(salary.getOtSalary())));
            payrollDisplayDto.setFineAmount(String.format("%,.0f",Double.valueOf(salary.getFineAmount())));
            payrollDisplayDto.setSalaryBonus(String.format("%,.0f",Double.valueOf(salary.getBonus())));
            payrollDisplayDto.setTax(String.format("%,.0f",Double.valueOf(salary.getTax())));
            payrollDisplayDto.setSocialInsurance(String.format("%,.0f",Double.valueOf(salary.getSocialInsurance())));
            payrollDisplayDto.setActuallyReceived(String.format("%,.0f",Double.valueOf(salary.getActuallyReceived())));

//            String body = "Total work day: " + payrollDisplayDto.getTotalWork() + "\n";
//            body += "Actual work day: " + payrollDisplayDto.getActualWork() + "\n";
//            body += "Earnings" + "\n";
//            body += "\t"+"Basic salary: " + payrollDisplayDto.getBasicSalary() + "\n";
//            body += "\t"+"Bonus salary: " + payrollDisplayDto.getSalaryBonus() + "\n";
//            body += "\t"+"OT salary: " + payrollDisplayDto.getOtSalary() + "\n";
//            body += "Deductions" + "\n";
//            body += "\t"+"Tax: " + payrollDisplayDto.getTax() + "\n";
//            body += "\t"+"Social Insurance (10.5%): " + payrollDisplayDto.getSocialInsurance() + "\n";
//            body += "Net Income: " + payrollDisplayDto.getActuallyReceived() + "\n";
            String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                    "    <meta name=\"x-apple-disable-message-reformatting\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                    "    <title></title>\n" +
                    "    <!--[if (mso 16)]>\n" +
                    "    <style type=\"text/css\">\n" +
                    "    a {text-decoration: none;}\n" +
                    "    </style>\n" +
                    "    <![endif]-->\n" +
                    "    <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]-->\n" +
                    "    <!--[if gte mso 9]>\n" +
                    "<xml>\n" +
                    "    <o:OfficeDocumentSettings>\n" +
                    "    <o:AllowPNG></o:AllowPNG>\n" +
                    "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                    "    </o:OfficeDocumentSettings>\n" +
                    "</xml>\n" +
                    "<![endif]-->\n" +
                    "    <!--[if !mso]><!-- -->\n" +
                    "    <link href=\"https://fonts.googleapis.com/css2?family=Oswald:wght@500&display=swap\" rel=\"stylesheet\">\n" +
                    "    <!--<![endif]-->\n" +
                    "<style>#outlook a {\n" +
                    "    padding: 0;\n" +
                    "}\n" +
                    "\n" +
                    ".es-button {\n" +
                    "    mso-style-priority: 100 !important;\n" +
                    "    text-decoration: none !important;\n" +
                    "}\n" +
                    "\n" +
                    "a[x-apple-data-detectors] {\n" +
                    "    color: inherit !important;\n" +
                    "    text-decoration: none !important;\n" +
                    "    font-size: inherit !important;\n" +
                    "    font-family: inherit !important;\n" +
                    "    font-weight: inherit !important;\n" +
                    "    line-height: inherit !important;\n" +
                    "}\n" +
                    "\n" +
                    ".es-desk-hidden {\n" +
                    "    display: none;\n" +
                    "    float: left;\n" +
                    "    overflow: hidden;\n" +
                    "    width: 0;\n" +
                    "    max-height: 0;\n" +
                    "    line-height: 0;\n" +
                    "    mso-hide: all;\n" +
                    "}\n" +
                    "\n" +
                    "[data-ogsb] .es-button {\n" +
                    "    border-width: 0 !important;\n" +
                    "    padding: 10px 30px 10px 30px !important;\n" +
                    "}\n" +
                    "\n" +
                    "/*\n" +
                    "END OF IMPORTANT\n" +
                    "*/\n" +
                    "body {\n" +
                    "    width: 100%;\n" +
                    "    font-family: arial, 'helvetica neue', helvetica, sans-serif;\n" +
                    "    -webkit-text-size-adjust: 100%;\n" +
                    "    -ms-text-size-adjust: 100%;\n" +
                    "}\n" +
                    "\n" +
                    "table {\n" +
                    "    mso-table-lspace: 0pt;\n" +
                    "    mso-table-rspace: 0pt;\n" +
                    "    border-collapse: collapse;\n" +
                    "    border-spacing: 0px;\n" +
                    "}\n" +
                    "\n" +
                    "table td,\n" +
                    "body,\n" +
                    ".es-wrapper {\n" +
                    "    padding: 0;\n" +
                    "    Margin: 0;\n" +
                    "}\n" +
                    "\n" +
                    ".es-content,\n" +
                    ".es-header,\n" +
                    ".es-footer {\n" +
                    "    table-layout: fixed !important;\n" +
                    "    width: 100%;\n" +
                    "}\n" +
                    "\n" +
                    "img {\n" +
                    "    display: block;\n" +
                    "    border: 0;\n" +
                    "    outline: none;\n" +
                    "    text-decoration: none;\n" +
                    "    -ms-interpolation-mode: bicubic;\n" +
                    "}\n" +
                    "\n" +
                    "p,\n" +
                    "hr {\n" +
                    "    Margin: 0;\n" +
                    "}\n" +
                    "\n" +
                    "h1,\n" +
                    "h2,\n" +
                    "h3,\n" +
                    "h4,\n" +
                    "h5 {\n" +
                    "    Margin: 0;\n" +
                    "    line-height: 120%;\n" +
                    "    mso-line-height-rule: exactly;\n" +
                    "    font-family: Oswald, sans-serif;\n" +
                    "}\n" +
                    "\n" +
                    "p,\n" +
                    "ul li,\n" +
                    "ol li,\n" +
                    "a {\n" +
                    "    -webkit-text-size-adjust: none;\n" +
                    "    -ms-text-size-adjust: none;\n" +
                    "    mso-line-height-rule: exactly;\n" +
                    "}\n" +
                    "\n" +
                    ".es-left {\n" +
                    "    float: left;\n" +
                    "}\n" +
                    "\n" +
                    ".es-right {\n" +
                    "    float: right;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p5 {\n" +
                    "    padding: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p5t {\n" +
                    "    padding-top: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p5b {\n" +
                    "    padding-bottom: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p5l {\n" +
                    "    padding-left: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p5r {\n" +
                    "    padding-right: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p10 {\n" +
                    "    padding: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p10t {\n" +
                    "    padding-top: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p10b {\n" +
                    "    padding-bottom: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p10l {\n" +
                    "    padding-left: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p10r {\n" +
                    "    padding-right: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p15 {\n" +
                    "    padding: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p15t {\n" +
                    "    padding-top: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p15b {\n" +
                    "    padding-bottom: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p15l {\n" +
                    "    padding-left: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p15r {\n" +
                    "    padding-right: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p20 {\n" +
                    "    padding: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p20t {\n" +
                    "    padding-top: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p20b {\n" +
                    "    padding-bottom: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p20l {\n" +
                    "    padding-left: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p20r {\n" +
                    "    padding-right: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p25 {\n" +
                    "    padding: 25px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p25t {\n" +
                    "    padding-top: 25px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p25b {\n" +
                    "    padding-bottom: 25px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p25l {\n" +
                    "    padding-left: 25px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p25r {\n" +
                    "    padding-right: 25px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p30 {\n" +
                    "    padding: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p30t {\n" +
                    "    padding-top: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p30b {\n" +
                    "    padding-bottom: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p30l {\n" +
                    "    padding-left: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p30r {\n" +
                    "    padding-right: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p35 {\n" +
                    "    padding: 35px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p35t {\n" +
                    "    padding-top: 35px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p35b {\n" +
                    "    padding-bottom: 35px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p35l {\n" +
                    "    padding-left: 35px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p35r {\n" +
                    "    padding-right: 35px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p40 {\n" +
                    "    padding: 40px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p40t {\n" +
                    "    padding-top: 40px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p40b {\n" +
                    "    padding-bottom: 40px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p40l {\n" +
                    "    padding-left: 40px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-p40r {\n" +
                    "    padding-right: 40px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-menu td {\n" +
                    "    border: 0;\n" +
                    "}\n" +
                    "\n" +
                    ".es-menu td a img {\n" +
                    "    display: inline-block !important;\n" +
                    "    vertical-align: middle;\n" +
                    "}\n" +
                    "\n" +
                    "/*\n" +
                    "END CONFIG STYLES\n" +
                    "*/\n" +
                    "s {\n" +
                    "    text-decoration: line-through;\n" +
                    "}\n" +
                    "\n" +
                    "p,\n" +
                    "ul li,\n" +
                    "ol li {\n" +
                    "    font-family: arial, 'helvetica neue', helvetica, sans-serif;\n" +
                    "    line-height: 150%;\n" +
                    "}\n" +
                    "\n" +
                    "ul li,\n" +
                    "ol li {\n" +
                    "    Margin-bottom: 15px;\n" +
                    "    margin-left: 0;\n" +
                    "}\n" +
                    "\n" +
                    "a {\n" +
                    "    text-decoration: none;\n" +
                    "}\n" +
                    "\n" +
                    ".es-menu td a {\n" +
                    "    text-decoration: none;\n" +
                    "    display: block;\n" +
                    "    font-family: arial, 'helvetica neue', helvetica, sans-serif;\n" +
                    "}\n" +
                    "\n" +
                    ".es-wrapper {\n" +
                    "    width: 100%;\n" +
                    "    height: 100%;\n" +
                    "    background-repeat: repeat;\n" +
                    "    background-position: center top;\n" +
                    "    background-image: url('https://ntnnqx.stripocdn.email/content/guids/CABINET_09d97977b4ac2ddd5501781f84ebc4a6/images/frame23.jpg');\n" +
                    "}\n" +
                    "\n" +
                    ".es-wrapper-color,\n" +
                    ".es-wrapper {\n" +
                    "    background-color: #9fc5e8;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header {\n" +
                    "    background-color: transparent;\n" +
                    "    background-repeat: repeat;\n" +
                    "    background-position: center top;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body {\n" +
                    "    background-color: #fef3e1;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body p,\n" +
                    ".es-header-body ul li,\n" +
                    ".es-header-body ol li {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 14px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body a {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 14px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-content-body {\n" +
                    "    background-color: #ffffff;\n" +
                    "}\n" +
                    "\n" +
                    ".es-content-body p,\n" +
                    ".es-content-body ul li,\n" +
                    ".es-content-body ol li {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 16px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-content-body a {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 16px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-footer {\n" +
                    "    background-color: transparent;\n" +
                    "    background-repeat: repeat;\n" +
                    "    background-position: center top;\n" +
                    "}\n" +
                    "\n" +
                    ".es-footer-body {\n" +
                    "    background-color: transparent;\n" +
                    "}\n" +
                    "\n" +
                    ".es-footer-body p,\n" +
                    ".es-footer-body ul li,\n" +
                    ".es-footer-body ol li {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 12px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-footer-body a {\n" +
                    "    color: #363E44;\n" +
                    "    font-size: 12px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-infoblock,\n" +
                    ".es-infoblock p,\n" +
                    ".es-infoblock ul li,\n" +
                    ".es-infoblock ol li {\n" +
                    "    line-height: 120%;\n" +
                    "    font-size: 12px;\n" +
                    "    color: #cccccc;\n" +
                    "}\n" +
                    "\n" +
                    ".es-infoblock a {\n" +
                    "    font-size: 12px;\n" +
                    "    color: #cccccc;\n" +
                    "}\n" +
                    "\n" +
                    "h1 {\n" +
                    "    font-size: 48px;\n" +
                    "    font-style: normal;\n" +
                    "    font-weight: bold;\n" +
                    "    color: #363E44;\n" +
                    "}\n" +
                    "\n" +
                    "h2 {\n" +
                    "    font-size: 30px;\n" +
                    "    font-style: normal;\n" +
                    "    font-weight: bold;\n" +
                    "    color: #363E44;\n" +
                    "}\n" +
                    "\n" +
                    "h3 {\n" +
                    "    font-size: 22px;\n" +
                    "    font-style: normal;\n" +
                    "    font-weight: bold;\n" +
                    "    color: #363E44;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body h1 a,\n" +
                    ".es-content-body h1 a,\n" +
                    ".es-footer-body h1 a {\n" +
                    "    font-size: 48px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body h2 a,\n" +
                    ".es-content-body h2 a,\n" +
                    ".es-footer-body h2 a {\n" +
                    "    font-size: 30px;\n" +
                    "}\n" +
                    "\n" +
                    ".es-header-body h3 a,\n" +
                    ".es-content-body h3 a,\n" +
                    ".es-footer-body h3 a {\n" +
                    "    font-size: 22px;\n" +
                    "}\n" +
                    "\n" +
                    "a.es-button,\n" +
                    "button.es-button {\n" +
                    "    border-style: solid;\n" +
                    "    border-color: #D87355;\n" +
                    "    border-width: 10px 30px 10px 30px;\n" +
                    "    display: inline-block;\n" +
                    "    background: #D87355;\n" +
                    "    border-radius: 30px;\n" +
                    "    font-size: 18px;\n" +
                    "    font-family: arial, 'helvetica neue', helvetica, sans-serif;\n" +
                    "    font-weight: normal;\n" +
                    "    font-style: normal;\n" +
                    "    line-height: 120%;\n" +
                    "    color: #ffffff;\n" +
                    "    text-decoration: none;\n" +
                    "    width: auto;\n" +
                    "    text-align: center;\n" +
                    "}\n" +
                    "\n" +
                    ".es-button-border {\n" +
                    "    border-style: solid solid solid solid;\n" +
                    "    border-color: #2cb543 #2cb543 #2cb543 #2cb543;\n" +
                    "    background: #D87355;\n" +
                    "    border-width: 0px 0px 0px 0px;\n" +
                    "    display: inline-block;\n" +
                    "    border-radius: 30px;\n" +
                    "    width: auto;\n" +
                    "}\n" +
                    "\n" +
                    ".msohide {\n" +
                    "    mso-hide: all;\n" +
                    "}\n" +
                    "\n" +
                    "/* RESPONSIVE STYLES Please do not delete and edit CSS styles below. If you don't need responsive layout, please delete this section. */\n" +
                    "@media only screen and (max-width: 600px) {\n" +
                    "\n" +
                    "    p,\n" +
                    "    ul li,\n" +
                    "    ol li,\n" +
                    "    a {\n" +
                    "        line-height: 150% !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    h1,\n" +
                    "    h2,\n" +
                    "    h3,\n" +
                    "    h1 a,\n" +
                    "    h2 a,\n" +
                    "    h3 a {\n" +
                    "        line-height: 120%;\n" +
                    "    }\n" +
                    "\n" +
                    "    h1 {\n" +
                    "        font-size: 48px !important;\n" +
                    "        text-align: center;\n" +
                    "    }\n" +
                    "\n" +
                    "    h2 {\n" +
                    "        font-size: 24px !important;\n" +
                    "        text-align: center;\n" +
                    "    }\n" +
                    "\n" +
                    "    h3 {\n" +
                    "        font-size: 20px !important;\n" +
                    "        text-align: left;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-header-body h1 a,\n" +
                    "    .es-content-body h1 a,\n" +
                    "    .es-footer-body h1 a {\n" +
                    "        font-size: 48px !important;\n" +
                    "        text-align: center;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-header-body h2 a,\n" +
                    "    .es-content-body h2 a,\n" +
                    "    .es-footer-body h2 a {\n" +
                    "        font-size: 24px !important;\n" +
                    "        text-align: center;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-header-body h3 a,\n" +
                    "    .es-content-body h3 a,\n" +
                    "    .es-footer-body h3 a {\n" +
                    "        font-size: 20px !important;\n" +
                    "        text-align: left;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-menu td a {\n" +
                    "        font-size: 12px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-header-body p,\n" +
                    "    .es-header-body ul li,\n" +
                    "    .es-header-body ol li,\n" +
                    "    .es-header-body a {\n" +
                    "        font-size: 14px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-content-body p,\n" +
                    "    .es-content-body ul li,\n" +
                    "    .es-content-body ol li,\n" +
                    "    .es-content-body a {\n" +
                    "        font-size: 14px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-footer-body p,\n" +
                    "    .es-footer-body ul li,\n" +
                    "    .es-footer-body ol li,\n" +
                    "    .es-footer-body a {\n" +
                    "        font-size: 14px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-infoblock p,\n" +
                    "    .es-infoblock ul li,\n" +
                    "    .es-infoblock ol li,\n" +
                    "    .es-infoblock a {\n" +
                    "        font-size: 12px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    *[class=\"gmail-fix\"] {\n" +
                    "        display: none !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-txt-c,\n" +
                    "    .es-m-txt-c h1,\n" +
                    "    .es-m-txt-c h2,\n" +
                    "    .es-m-txt-c h3 {\n" +
                    "        text-align: center !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-txt-r,\n" +
                    "    .es-m-txt-r h1,\n" +
                    "    .es-m-txt-r h2,\n" +
                    "    .es-m-txt-r h3 {\n" +
                    "        text-align: right !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-txt-l,\n" +
                    "    .es-m-txt-l h1,\n" +
                    "    .es-m-txt-l h2,\n" +
                    "    .es-m-txt-l h3 {\n" +
                    "        text-align: left !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-txt-r img,\n" +
                    "    .es-m-txt-c img,\n" +
                    "    .es-m-txt-l img {\n" +
                    "        display: inline !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-button-border {\n" +
                    "        display: inline-block !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    a.es-button,\n" +
                    "    button.es-button {\n" +
                    "        font-size: 18px !important;\n" +
                    "        display: inline-block !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-adaptive table,\n" +
                    "    .es-left,\n" +
                    "    .es-right {\n" +
                    "        width: 100% !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-content table,\n" +
                    "    .es-header table,\n" +
                    "    .es-footer table,\n" +
                    "    .es-content,\n" +
                    "    .es-footer,\n" +
                    "    .es-header {\n" +
                    "        width: 100% !important;\n" +
                    "        max-width: 600px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-adapt-td {\n" +
                    "        display: block !important;\n" +
                    "        width: 100% !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .adapt-img {\n" +
                    "        width: 100% !important;\n" +
                    "        height: auto !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p0 {\n" +
                    "        padding: 0 !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p0r {\n" +
                    "        padding-right: 0 !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p0l {\n" +
                    "        padding-left: 0 !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p0t {\n" +
                    "        padding-top: 0 !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p0b {\n" +
                    "        padding-bottom: 0 !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p20b {\n" +
                    "        padding-bottom: 20px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-mobile-hidden,\n" +
                    "    .es-hidden {\n" +
                    "        display: none !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    tr.es-desk-hidden,\n" +
                    "    td.es-desk-hidden,\n" +
                    "    table.es-desk-hidden {\n" +
                    "        width: auto !important;\n" +
                    "        overflow: visible !important;\n" +
                    "        float: none !important;\n" +
                    "        max-height: inherit !important;\n" +
                    "        line-height: inherit !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    tr.es-desk-hidden {\n" +
                    "        display: table-row !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    table.es-desk-hidden {\n" +
                    "        display: table !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    td.es-desk-menu-hidden {\n" +
                    "        display: table-cell !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-menu td {\n" +
                    "        width: 1% !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    table.es-table-not-adapt,\n" +
                    "    .esd-block-html table {\n" +
                    "        width: auto !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    table.es-social {\n" +
                    "        display: inline-block !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    table.es-social td {\n" +
                    "        display: inline-block !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p5 {\n" +
                    "        padding: 5px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p5t {\n" +
                    "        padding-top: 5px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p5b {\n" +
                    "        padding-bottom: 5px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p5r {\n" +
                    "        padding-right: 5px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p5l {\n" +
                    "        padding-left: 5px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p10 {\n" +
                    "        padding: 10px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p10t {\n" +
                    "        padding-top: 10px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p10b {\n" +
                    "        padding-bottom: 10px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p10r {\n" +
                    "        padding-right: 10px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p10l {\n" +
                    "        padding-left: 10px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p15 {\n" +
                    "        padding: 15px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p15t {\n" +
                    "        padding-top: 15px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p15b {\n" +
                    "        padding-bottom: 15px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p15r {\n" +
                    "        padding-right: 15px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p15l {\n" +
                    "        padding-left: 15px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p20 {\n" +
                    "        padding: 20px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p20t {\n" +
                    "        padding-top: 20px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p20r {\n" +
                    "        padding-right: 20px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p20l {\n" +
                    "        padding-left: 20px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p25 {\n" +
                    "        padding: 25px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p25t {\n" +
                    "        padding-top: 25px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p25b {\n" +
                    "        padding-bottom: 25px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p25r {\n" +
                    "        padding-right: 25px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p25l {\n" +
                    "        padding-left: 25px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p30 {\n" +
                    "        padding: 30px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p30t {\n" +
                    "        padding-top: 30px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p30b {\n" +
                    "        padding-bottom: 30px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p30r {\n" +
                    "        padding-right: 30px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p30l {\n" +
                    "        padding-left: 30px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p35 {\n" +
                    "        padding: 35px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p35t {\n" +
                    "        padding-top: 35px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p35b {\n" +
                    "        padding-bottom: 35px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p35r {\n" +
                    "        padding-right: 35px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p35l {\n" +
                    "        padding-left: 35px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p40 {\n" +
                    "        padding: 40px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p40t {\n" +
                    "        padding-top: 40px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p40b {\n" +
                    "        padding-bottom: 40px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p40r {\n" +
                    "        padding-right: 40px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-m-p40l {\n" +
                    "        padding-left: 40px !important;\n" +
                    "    }\n" +
                    "\n" +
                    "    .es-desk-hidden {\n" +
                    "        display: table-row !important;\n" +
                    "        width: auto !important;\n" +
                    "        overflow: visible !important;\n" +
                    "        max-height: inherit !important;\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "/* END RESPONSIVE STYLES */\n" +
                    "html,\n" +
                    "body {\n" +
                    "    font-family: arial, 'helvetica neue', helvetica, sans-serif;\n" +
                    "}</style>"+
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"es-wrapper-color\">\n" +
                    "        <!--[if gte mso 9]>\n" +
                    "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                    "\t\t\t\t<v:fill type=\"tile\" src=\"https://ntnnqx.stripocdn.email/content/guids/CABINET_09d97977b4ac2ddd5501781f84ebc4a6/images/frame23.jpg\" color=\"#9fc5e8\" origin=\"0.5, 0\" position=\"0.5, 0\"></v:fill>\n" +
                    "\t\t\t</v:background>\n" +
                    "\t\t<![endif]-->\n" +
                    "        <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" background=\"https://ntnnqx.stripocdn.email/content/guids/CABINET_09d97977b4ac2ddd5501781f84ebc4a6/images/frame23.jpg\" style=\"background-position: center top;\">\n" +
                    "            <tbody>\n" +
                    "                <tr>\n" +
                    "                    <td class=\"esd-email-paddings\" valign=\"top\">\n" +
                    "                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"esd-header-popover es-header\" align=\"center\">\n" +
                    "                            <tbody>\n" +
                    "                                <tr>\n" +
                    "                                    <td class=\"esd-stripe\" align=\"center\">\n" +
                    "                                        <table bgcolor=\"#ffffff\" class=\"es-header-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                    "                                            <tbody>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p40t es-p30b es-p20r es-p20l\" align=\"left\" bgcolor=\"#9fc5e8\" style=\"background-color: #9fc5e8;\">\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"560\" class=\"es-m-p0r esd-container-frame\" valign=\"top\" align=\"center\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"center\" class=\"esd-block-image\" style=\"font-size: 0px;\"><a target=\"_blank\"><img class=\"adapt-img\" src=\"https://ntnnqx.stripocdn.email/content/guids/CABINET_09d97977b4ac2ddd5501781f84ebc4a6/images/minswaplogo.jpg\" alt style=\"display: block;\" width=\"560\"></a></td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                            </tbody>\n" +
                    "                                        </table>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                            </tbody>\n" +
                    "                        </table>\n" +
                    "                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\">\n" +
                    "                            <tbody>\n" +
                    "                                <tr>\n" +
                    "                                    <td class=\"esd-stripe\" align=\"center\">\n" +
                    "                                        <table bgcolor=\"#fef3e1\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #fef3e1;\">\n" +
                    "                                            <tbody>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p30t es-p20r es-p20l\" align=\"left\" bgcolor=\"#9fc5e8\" style=\"background-color: #9fc5e8;\">\n" +
                    "                                                        <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"270\" class=\"es-m-p20b esd-container-frame\" align=\"left\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left:1px solid #333333;border-right:1px solid #333333;border-top:1px solid #333333;border-bottom:1px solid #333333;border-radius: 15px; border-collapse: separate;\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text es-p15\">\n" +
                    "                                                                                        <h2>Total work day:"+ payrollDisplayDto.getTotalWork()+"</h2>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td><td width=\"20\"></td><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr class=\"es-mobile-hidden\">\n" +
                    "                                                                    <td width=\"270\" align=\"left\" class=\"esd-container-frame\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text es-p15\">\n" +
                    "                                                                                        <h2>Actual work day: "+payrollDisplayDto.getActualWork()+"</h2>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td></tr></table><![endif]-->\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p20t es-p20r es-p20l\" align=\"left\" bgcolor=\"#9fc5e8\" style=\"background-color: #9fc5e8;\">\n" +
                    "                                                        <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"250\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"250\" class=\"esd-container-frame es-m-p20b\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text\">\n" +
                    "                                                                                        <h3>Earnings</h3>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td><td width=\"20\"></td><td width=\"290\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"290\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left: 1px solid #666666;\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text es-p10r es-p10l\">\n" +
                    "                                                                                        <p>Basic salary: "+payrollDisplayDto.getBasicSalary()+"<br>Bonus salary: "+payrollDisplayDto.getSalaryBonus()+"<br>OT salary: "+payrollDisplayDto.getOtSalary()+"</p>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td></tr></table><![endif]-->\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p30t es-p20r es-p20l\" align=\"left\" bgcolor=\"#9fc5e8\" style=\"background-color: #9fc5e8;\">\n" +
                    "                                                        <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"250\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"250\" class=\"esd-container-frame es-m-p20b\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text\">\n" +
                    "                                                                                        <h3>Deductions</h3>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td><td width=\"20\"></td><td width=\"290\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"290\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left: 1px solid #666666;\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text es-p10r es-p10l\">\n" +
                    "                                                                                        <p>Tax: "+payrollDisplayDto.getTax()+"<br>Social Insurance (10.5%): "+payrollDisplayDto.getSocialInsurance()+"</p>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td></tr></table><![endif]-->\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p30t es-p30b es-p20r es-p20l\" align=\"left\" bgcolor=\"#9fc5e8\" style=\"background-color: #9fc5e8;\">\n" +
                    "                                                        <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"250\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"250\" class=\"esd-container-frame es-m-p20b\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text\">\n" +
                    "                                                                                        <h3>Net Income</h3>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td><td width=\"20\"></td><td width=\"290\" valign=\"top\"><![endif]-->\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"290\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left: 1px solid #666666;\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"left\" class=\"esd-block-text es-p10r es-p10l\">\n" +
                    "                                                                                        <p>"+payrollDisplayDto.getActuallyReceived()+"</p>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                        <!--[if mso]></td></tr></table><![endif]-->\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                            </tbody>\n" +
                    "                                        </table>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                            </tbody>\n" +
                    "                        </table>\n" +
                    "                        <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content esd-footer-popover\" align=\"center\">\n" +
                    "                            <tbody>\n" +
                    "                                <tr>\n" +
                    "                                    <td class=\"esd-stripe\" align=\"center\">\n" +
                    "                                        <table bgcolor=\"#fef3e1\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                    "                                            <tbody>\n" +
                    "                                                <tr>\n" +
                    "                                                    <td class=\"esd-structure es-p30t es-p20r es-p20l\" align=\"left\">\n" +
                    "                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                            <tbody>\n" +
                    "                                                                <tr>\n" +
                    "                                                                    <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                    "                                                                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                                                            <tbody>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"center\" class=\"esd-block-image es-p10b\" style=\"font-size: 0px;\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://ntnnqx.stripocdn.email/content/guids/CABINET_0c21def5ccfe2acbb9636d419f2c75b2/images/nounidea4800207_1_PSp.png\" alt=\"Idea\" style=\"display: block;\" height=\"65\" title=\"Idea\"></a></td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                                <tr>\n" +
                    "                                                                                    <td align=\"center\" class=\"esd-block-text\">\n" +
                    "                                                                                        <h2>Human Resources Management System</h2>\n" +
                    "                                                                                    </td>\n" +
                    "                                                                                </tr>\n" +
                    "                                                                            </tbody>\n" +
                    "                                                                        </table>\n" +
                    "                                                                    </td>\n" +
                    "                                                                </tr>\n" +
                    "                                                            </tbody>\n" +
                    "                                                        </table>\n" +
                    "                                                    </td>\n" +
                    "                                                </tr>\n" +
                    "                                            </tbody>\n" +
                    "                                        </table>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                            </tbody>\n" +
                    "                        </table>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "            </tbody>\n" +
                    "        </table>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            //send mail
            String toMail = userPrincipal.getEmail();
            emailSenderService.sendEmail(toMail, "Payslip " + month + "/" + year, body);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }catch (Exception e){
            responseEntity = BaseResponse.ofFailedCustom(Meta.buildMeta(SEND_FAIL, null), null);
        }
        return responseEntity;
    }
}
