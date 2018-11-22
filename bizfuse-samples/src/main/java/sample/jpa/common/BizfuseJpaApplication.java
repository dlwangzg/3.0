package sample.jpa.common;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.leadingsoft.bizfuse.common.web.annotation.EnableBizfuseWebMVC;
import com.leadingsoft.bizfuse.common.web.config.BizfuseWebProperties;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

import sample.jpa.common.model.Student;
import sample.jpa.common.model.Teacher;
import sample.jpa.common.repository.StudentRepository;
import sample.jpa.common.repository.TeacherRepository;

@EnableBizfuseWebMVC
@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({BizfuseWebProperties.class })
public class BizfuseJpaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BizfuseJpaApplication.class);

    private static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";
    private static final String BIZFUSE_WEBAUTH_PROFILE = "bizfusejpa,dev";

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved
     *         into an address
     */
    public static void main(final String[] args) throws UnknownHostException {
        final SpringApplication app = new SpringApplication(BizfuseJpaApplication.class);

        // 手动添加默认profiles
        final Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(BizfuseJpaApplication.SPRING_PROFILE_ACTIVE,
                BizfuseJpaApplication.BIZFUSE_WEBAUTH_PROFILE);
        app.setDefaultProperties(defProperties);

        final Environment env = app.run(args).getEnvironment();
        BizfuseJpaApplication.log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! \n----------------------------------------------------------",
                env.getProperty("spring.application.name"));
    }

    @Override
    public void run(final String... arg0) throws Exception {
        final Teacher zhsf = new Teacher();
        zhsf.setName("张三丰");
        zhsf.setAge(100);
        this.teacherRepository.save(zhsf);

        final Teacher xiexun = new Teacher();
        xiexun.setName("谢逊");
        xiexun.setAge(50);
        this.teacherRepository.save(xiexun);

        final Student songyq = new Student();
        songyq.setName("宋远桥");
        songyq.setGrade(4);
        songyq.getTeachers().add(zhsf);
        this.studentRepository.save(songyq);

        final Student zhcs = new Student();
        zhcs.setName("张翠山");
        zhcs.setGrade(3);
        zhcs.setMoney(new BigDecimal("123456.1234567"));
        zhcs.getTeachers().add(zhsf);
        zhcs.getTeachers().add(xiexun);
        this.studentRepository.save(zhcs);

        final Student zhwj = new Student();
        zhwj.setName("张无忌");
        zhwj.setGrade(1);
        zhwj.getTeachers().add(zhsf);
        zhwj.getTeachers().add(xiexun);
        this.studentRepository.save(zhwj);

        final Student yinlt = new Student();
        yinlt.setName("殷梨亭");
        yinlt.setGrade(2);
        yinlt.getTeachers().add(zhsf);
        this.studentRepository.save(yinlt);

        final long count = this.studentRepository.findAll().size();
        System.out.println(String.format("Student count : %s", count));
        final PageRequest pageable = new PageRequest(0, 2);
        final Searchable searchable = new Searchable();
        searchable.put("name", "张");
        final Page<Student> students = this.studentRepository.searchPage(pageable, searchable);
        System.out.println(JsonUtils.pojoToJson(students));
    }
}
