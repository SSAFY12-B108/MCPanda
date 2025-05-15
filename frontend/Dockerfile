# 1. 빌드 단계
FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# 2. 프로덕션 실행 단계
FROM node:20-alpine

WORKDIR /app

ENV NODE_ENV=production

# standalone output에서 필요한 파일들 복사
# /app/public 폴더 복사
COPY --from=builder /app/public ./public
# /app/.next/standalone 폴더를 현재 디렉토리(/app)로 복사
COPY --from=builder /app/.next/standalone ./
# /app/.next/static 폴더를 현재 디렉토리의 .next 폴더 아래로 복사
COPY --from=builder /app/.next/static ./.next/static

# (선택 사항이지만 권장) 보안을 위해 non-root 사용자 사용
# Docker 이미지에 해당 사용자가 미리 정의되어 있거나, 여기서 생성해야 합니다.
# 공식 Next.js 이미지는 nextjs 사용자를 제공합니다. (FROM node:20-alpine 대신 FROM nextjs/node:20-alpine 사용 고려)
# RUN addgroup --system --gid 1001 nodejs
# RUN adduser --system --uid 1001 nextjs
# USER nextjs

EXPOSE 3000

# standalone output은 server.js 파일을 직접 실행합니다.
CMD ["node", "server.js"]