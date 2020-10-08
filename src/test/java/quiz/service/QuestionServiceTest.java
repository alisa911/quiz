package quiz.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import quiz.domain.Question;
import quiz.exception.exceptions.AlreadyExistException;
import quiz.exception.exceptions.CustomNotFoundException;
import quiz.exception.exceptions.NoOneTrueAnswerException;
import quiz.exception.exceptions.SeveralTrueAnswersException;
import quiz.repository.AnswerRepository;
import quiz.repository.QuestionRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static quiz.TestData.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:test")
class QuestionServiceTest {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    Long id1;

    @BeforeEach
    void setUp() {
        questionRepository.save(QUESTION_1);
        ANSWER_2.setQuestion(QUESTION_1);
        answerRepository.save(ANSWER_2);
        questionRepository.save(QUESTION_2);
        id1 = QUESTION_1.getId();
    }

    //get
    @Test
    void get() {
        Question actualQuestion = questionService.get(id1);
        assertMatch(actualQuestion, QUESTION_1);
    }

    @Test
    void getNotFound() {
        assertThrows(CustomNotFoundException.class, () ->
                questionService.get(BIG_ID));
    }

    @Test
    void getAll() {
        List<Question> expectedQuestions = List.of(QUESTION_1, QUESTION_2);
        List<Question> actualQuestions = questionService.getAll();

        assertMatch(actualQuestions, expectedQuestions);
    }

    //delete
    @Test
    void delete() {
        questionService.delete(id1);
        List<Question> expectedQuestions = List.of(QUESTION_2);
        List<Question> actualQuestions = questionService.getAll();

        assertMatch(actualQuestions, expectedQuestions);
    }

    //create
    @Test
    void create() {
        QUESTION_3.setAnswers(Set.of(ANSWER_1));
        questionService.create(QUESTION_3);
        List<Question> expectedQuestions = List.of(QUESTION_1, QUESTION_2, QUESTION_3);
        List<Question> actualQuestions = questionService.getAll();

        assertMatch(actualQuestions, expectedQuestions);
    }

    @Test
    void createAlreadyExist(){
        assertThrows(AlreadyExistException.class, () ->
                questionService.create(QUESTION_1));
    }

    @Test
    void createSeveralTrueAnswers(){
        QUESTION_3.setAnswers(Set.of(ANSWER_1, ANSWER_2));
        assertThrows(SeveralTrueAnswersException.class, () ->
                questionService.create(QUESTION_3));
    }

    @Test
    void createNoOneTrueAnswers(){
        QUESTION_3.setAnswers(Set.of(ANSWER_3, ANSWER_4));
        assertThrows(NoOneTrueAnswerException.class, () ->
                questionService.create(QUESTION_3));
    }

    //update
    @Test
    void update() {
        Question actualQuestion = questionService.get(id1);
        QUESTION_1.setQuestion(QUESTION_STRING);
        questionService.update(QUESTION_1, id1);

        assertMatch(actualQuestion, QUESTION_1);
    }

    @Test
    void updateNotFound() {
        assertThrows(CustomNotFoundException.class, () ->
                questionService.update(QUESTION_1, BIG_ID));
    }

    @Test
    void updateAlreadyExist(){
        assertThrows(AlreadyExistException.class, () ->
                questionService.update(QUESTION_2, id1));
    }

    @AfterEach
    public void cleanUpEach(){
        questionRepository.deleteAll();
        QUESTION_1.setId(null);
    }
}