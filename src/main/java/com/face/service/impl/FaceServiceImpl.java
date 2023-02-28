package com.face.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;
import com.face.mapper.FaceMapper;
import com.face.server.FaceContrastServer;
import com.face.service.FaceService;
import com.face.utils.JwtUtils;
import com.face.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaceServiceImpl extends ServiceImpl<FaceMapper, Face> implements FaceService {
    @Autowired
    FaceContrastServer faceContrastServer;
    @Override
    public FaceResult faceVef(String imageBase) {
        String image = JSONUtil.parseObj(imageBase).getStr("imageBase");
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list(); //Face::getVefNum face.getVerNum
        FaceResult faceState = null;
        //if the database is null, the method will be login.
        if (faceList.size() == 0){
            return initFace(imageBase);
        }else {
            int faceLength = faceList.size();
            for (Face face : faceList){
                FaceResult faceResult = faceContrastServer.faceContrast(face.getFaceBase(),image);
                // if it successful? this success only means detected a face
                if (faceResult.getCode() == FaceResult.SUCCESS_CODE){
                    // is the similarity greater than 80?
                    if (faceResult.getScore() >= FaceResult.SATISFY_SCORE ){ //update face set vefnum = vefnum+1 where fid = fid.
                        if (face.getFaceStatus() == 0){ //0 is ok, 1 is forbidden.
                            lambdaUpdate().set(Face::getVefNum,face.getVefNum()+1).eq(Face::getFid,face.getFid()).update(); // update return ture or false
                            faceResult.setMsg(TimeUtils.timeQuantum()+"好，"+face.getFaceName());
                            faceResult.setName(face.getFaceName());
                            Map<String, String> map = new HashMap<>();
                            map.put("score",String.valueOf(faceResult.getScore()));
                            map.put("faceName", faceResult.getName());
                            faceResult.setToken(JwtUtils.genereteToken(map)); //generate token
                            return faceResult;
                        }else { // faild , face is forbidden.
                            lambdaUpdate().set(Face::getVefNum, face.getVefNum()+1).eq(Face::getFid, face.getFid()).update();
                            faceResult.setMsg(face.getFaceName()+"The face is forbidden");
                            faceResult.setName(face.getFaceName());
                            faceResult.setCode(FaceResult.FORBIDDEN_FACE);
                            faceState = faceResult;
                            if (faceLength == 1){
                                return faceResult;
                            }
                            faceLength--;
                        }
                    }else {
                        if (faceLength == 1){
                            //if forbidden, note forbidden
                            // priority is forbidden > not detect face.
                            return faceState != null ? faceState : FaceResult.error(FaceResult.NOT_FOUND_FACE,"There is no face in the DataBase", faceResult.getScore());
                        }
                    }
                }else {
                    return faceResult;
                }
            }
        }
        return FaceResult.error(FaceResult.NULL_ERROR,"NullPointerError");
    }

    public FaceResult initFace(String imageBase){
        FaceResult faceResult = new FaceResult();
        Face face = new Face();
        face.setFaceBase(imageBase);
        face.setCreateTime(new Date());
        face.setFaceName("admin");
        face.setVefNum(0);
        face.setFaceStatus(0);
        boolean save = save(face); // save in mybatisplus is inster.
        faceResult.setCode(FaceResult.INIT_FACE);
        faceResult.setMsg("Initialize face"+(save ? "success":"failed")+","+(save? "Please login by verify":"Please try later"));
        faceResult.setName(face.getFaceName());
        return faceResult;
    }
}