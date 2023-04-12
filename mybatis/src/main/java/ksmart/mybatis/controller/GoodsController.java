package ksmart.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;
import ksmart.mybatis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

	private final GoodsService goodsService;
	private final MemberMapper memberMapper;
	private final GoodsMapper goodsMapper;
	
	public GoodsController(GoodsService goodsService, MemberMapper memberMapper, GoodsMapper goodsMapper) {
		this.goodsService = goodsService;
		this.goodsMapper = goodsMapper;
		this.memberMapper = memberMapper;
	}
	
	@PostMapping("/removeGoods")
	public String removeGoods(@RequestParam(name="goodsCode") String goodsCode
							 ,@RequestParam(name="memberId") String memberId
							 ,@RequestParam(name="memberPw") String memberPw
							 ,HttpSession session
							 ,RedirectAttributes reAttr) {
		String memberLevel = (String) session.getAttribute("SLEVEL");
		boolean isDelete = true;
		String msg = "";
		if(memberLevel != null && "2".equals(memberLevel)) {
			isDelete = goodsMapper.isSellerByGoodsCode(memberId, goodsCode);
		}
		
		Member member = memberMapper.getMemberInfoById(memberId);
		if(member != null) {
			String checkPw = member.getMemberPw();
			if(!checkPw.equals(memberPw)) isDelete = false;
		}
		if(isDelete) {
			goodsService.removeGoods(goodsCode);
			msg = "상품코드: "+ goodsCode + " 가 삭제되었습니다.";
		}else {
			msg = "상품코드: "+ goodsCode + " 가 삭제할 수 없습니다.";			
		}
		reAttr.addAttribute("msg", msg);
		
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/removeGoods")
	public String removeGoods(Model model
							 ,@RequestParam(name="goodsCode") String goodsCode) {
		model.addAttribute("title", "상품삭제");
		model.addAttribute("goodsCode", goodsCode);
		return "goods/removeGoods";
	}
	
	@PostMapping("/modifyGoods")
	public String modifyGoods(Goods goods) {
		goodsService.modifyGoods(goods);
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/modifyGoods")
	public String modifyGoods(Model model
							 ,@RequestParam(name="goodsCode") String goodsCode) {
		
		Goods goodsInfo = goodsService.getGoodsInfoByCode(goodsCode);
		
		model.addAttribute("title", "상품수정");
		model.addAttribute("goodsInfo", goodsInfo);
		
		return "goods/modifyGoods";
	}
	
	@PostMapping("/addGoods") //화면에서 넘어온 요청 경로와 방식을 따라 매핑된 메서드 실행
	public String addGoods(Goods goods) { //스트링 데이터타입을 반환하는 addGoods 메서드를 실행. 매개변수 타입은 Goods 이며, #addGoodsForm이하 요소들의 name과 value값이 dto Goods의 필드명과 데이터 타입에 부합하기 때문에, Spring boot 측에서 자동으로 담아준다(단, getter, setter 필수). 즉, @RequestParam이 필요 없다.
		goodsService.addGoods(goods); //goodsService에 있는 addGoods에 goods 매개변수를 넣어서 메서드 호출
		return "redirect:/goods/goodsList"; //페이지전환이 필요한 경우 redirect로 url재요청 -> 주소창에 [localhost:/goods/goodsList]라고 요청한 것과 같음. 따라서 다시 /goods 로 매핑 후 /goodsList로 get매핑
	}
	
	@PostMapping("/sellersInfo")
	@ResponseBody
	public List<Member> sellersInfo(){
		String searchKey = "m.m_level";
		String searchValue = "2";
		List<Member> memberList = memberMapper.getMemberList(searchKey, searchValue);
		memberList.forEach(seller -> seller.setMemberPw(""));
		return memberList;
	}
	
	@GetMapping("/addGoods") //요청된 주소 경로에 맞는 컨트롤러에 와서 매핑된 메서드를 실행
	public String addGoods(Model model) { //스트링 데이터타입을 반환하는 addGoods 메서드를 실행 
		
		model.addAttribute("title", "상품등록"); //addAttribute 메서드를 사용하여 model 객체에 키워드(스트링)과 키워드에 해당하는 값(object)을 담는다
		
		return "goods/addGoods"; //논리경로 문자열을 반환한다
	}
		
	@GetMapping("/sellerList")
	public String getGoodsListBySeller( Model model
									   ,@RequestParam(name="checkSearch", required = false) String[] checkArr
									   ,@RequestParam(name="searchValue", required = false) String searchValue) {
		
		List<Member> goodsListBySeller = goodsService.getGoodsListBySeller(checkArr, searchValue);
		model.addAttribute("title", "판매자별상품조회");
		model.addAttribute("goodsListBySeller", goodsListBySeller);
		
		return "goods/sellerList";
	}
	
	@GetMapping("/goodsList")
	public String getGoodsList(Model model
			   				  ,HttpSession session
			   				  ,@RequestParam(name="msg", required = false) String msg) {
		String memberLevel = (String) session.getAttribute("SLEVEL");
		Map<String, Object> paramMap = null;
		if(memberLevel != null && "2".equals(memberLevel)) {
			String sellerId = (String)session.getAttribute("SID");
			
			paramMap = new HashMap<String, Object>();
			paramMap.put("searchKey", "g_seller_id");
			paramMap.put("searchValue", sellerId);
		}
		
		List<Goods> goodsList = goodsService.getGoodsList(paramMap);
		
		model.addAttribute("title", "상품조회");
		model.addAttribute("goodsList", goodsList);
		if(msg != null) model.addAttribute("msg", msg);
		
		return "goods/goodsList";
	}
}
