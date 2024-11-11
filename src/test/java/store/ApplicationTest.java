package store;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.test.NsTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ApplicationTest extends NsTest {
    @Test
    void 파일에_있는_상품_목록_출력() {
        assertSimpleTest(() -> {
            run("[물-1]", "N", "N");
            assertThat(output()).contains(
                    "- 콜라 1,000원 10개 탄산2+1",
                    "- 콜라 1,000원 10개",
                    "- 사이다 1,000원 8개 탄산2+1",
                    "- 사이다 1,000원 7개",
                    "- 오렌지주스 1,800원 9개 MD추천상품",
                    "- 오렌지주스 1,800원 재고 없음",
                    "- 탄산수 1,200원 5개 탄산2+1",
                    "- 탄산수 1,200원 재고 없음",
                    "- 물 500원 10개",
                    "- 비타민워터 1,500원 6개",
                    "- 감자칩 1,500원 5개 반짝할인",
                    "- 감자칩 1,500원 5개",
                    "- 초코바 1,200원 5개 MD추천상품",
                    "- 초코바 1,200원 5개",
                    "- 에너지바 2,000원 5개",
                    "- 정식도시락 6,400원 8개",
                    "- 컵라면 1,700원 1개 MD추천상품",
                    "- 컵라면 1,700원 10개"
            );
        });
    }

    @Test
    void 여러_개의_일반_상품_구매() {
        assertSimpleTest(() -> {
            run("[비타민워터-3],[물-2],[정식도시락-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈18,300");
        });
    }

    @Test
    void 기간에_해당하지_않는_프로모션_적용() {
        assertNowTest(() -> {
            run("[감자칩-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈3,000");
        }, LocalDate.of(2024, 2, 1).atStartOfDay());
    }

    @Test
    void 프로모션_적용된_상품이지만_개수_초과로_일반_상품_함께_구매() {
        assertSimpleTest(() -> {
            run("[콜라-14]", "Y", "Y");
            assertThat(output().replaceAll("\\s", ""))
                    .contains("현재콜라5개는프로모션할인이적용되지않습니다.그래도구매하시겠습니까?(Y/N)")
                    .contains("콜라1414,000")
                    .contains("콜라3")
                    .contains("총구매액1414,000")
                    .contains("행사할인-3,000")
                    .contains("멤버십할인-1,500")
                    .contains("내실돈9,500");
        });
    }

    @Test
    void 프로모션_상품재고_초과_구매_시도_후_프로모션_적용된_상품_재고만큼만_구매() {
        assertSimpleTest(() -> {
            run("[콜라-14]", "N", "Y");
            assertThat(output().replaceAll("\\s", ""))
                    .contains("현재콜라5개는프로모션할인이적용되지않습니다.그래도구매하시겠습니까?(Y/N)")
                    .contains("콜라99,000")
                    .contains("콜라3")
                    .contains("총구매액99,000")
                    .contains("행사할인-3,000")
                    .contains("멤버십할인-0")
                    .contains("내실돈6,000");
        });
    }

    @Test
    void 프로모션_상품이지만_프로모션_적용수량에_해당되지않아서_안내_없이_구매() {
        assertSimpleTest(() -> {
            run("[콜라-1]", "Y");
            assertThat(output().replaceAll("\\s", ""))
                    .doesNotContain("무료로더받을수있습니다.추가하시겠습니까?(Y/N)")
                    .contains("콜라11,000")
                    .contains("총구매액11,000")
                    .contains("행사할인-0")
                    .contains("멤버십할인-300")
                    .contains("내실돈700")
                    .contains("감사합니다.구매하고싶은다른상품이있나요?");
        });
    }

    @Test
    void 멤버십할인의_최대값은_8천원이다() {
        assertSimpleTest(() -> {
            run("[콜라-20],[정식도시락-6],[에너지바-5],[물-10]", "Y", "Y");
            assertThat(output().replaceAll("\\s", ""))
                    .doesNotContain("무료로더받을수있습니다.추가하시겠습니까?")
                    .contains("총구매액4173,400")
                    .contains("행사할인-3,000")
                    .contains("멤버십할인-8,00")
                    .contains("내실돈62,400")
                    .contains("감사합니다.구매하고싶은다른상품이있나요?");
        });
    }

    @Test
    void 예외_테스트() {
        assertSimpleTest(() -> {
            runException("[컵라면-12]", "N", "N");
            assertThat(output()).contains("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        });
    }

    @Test
    void YES_OR_NO_예외_테스트() {
        assertSimpleTest(() -> {
            runException("[물-1]", "test", "N");
            assertThat(output()).contains("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        });
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}
