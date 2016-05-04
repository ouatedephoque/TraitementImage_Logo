import cv2
import numpy as np
import matplotlib.pyplot as plt
from os import listdir
from os.path import isfile, join
import sys

MIN_MATCH_COUNT = 10

def fileLogo():
	return [f for f in listdir("logo") if isfile(join("logo", f))]
	
def decorator(str):
	print("**************************************", end="\n\n")
	print("\t%s" % str, end="\n\n")
	print("**************************************", end="\n\n")

def findLogoMatch(imgToFind):
	nbMatches = 0
	img3 = None
	title = None
	img2 = cv2.imread(imgToFind,0) # trainImage

	listLogo = fileLogo()

	for logo in listLogo:
		decorator(logo)
		img1 = cv2.imread('logo\\'+logo,0)          # queryImage

		# Initiate SIFT detector
		sift = cv2.xfeatures2d.SIFT_create()

		# find the keypoints and descriptors with SIFT
		kp1, des1 = sift.detectAndCompute(img1,None)
		kp2, des2 = sift.detectAndCompute(img2,None)

		FLANN_INDEX_KDTREE = 0
		index_params = dict(algorithm = FLANN_INDEX_KDTREE, trees = 5)
		search_params = dict(checks = 50)

		flann = cv2.FlannBasedMatcher(index_params, search_params)

		matches = flann.knnMatch(des1,des2,k=2)

		# store all the good matches as per Lowe's ratio test.
		good = []
		for m,n in matches:
			if m.distance < 0.7*n.distance:
				good.append(m)
				
		print("%d points trouvés" % len(good), end="\n\n")
		if len(good) > nbMatches:
			if len(good)>MIN_MATCH_COUNT:
				src_pts = np.float32([ kp1[m.queryIdx].pt for m in good ]).reshape(-1,1,2)
				dst_pts = np.float32([ kp2[m.trainIdx].pt for m in good ]).reshape(-1,1,2)

				M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,5.0)
				
				if mask is not None :
					matchesMask = mask.ravel().tolist()

					h,w = img1.shape
					pts = np.float32([ [0,0],[0,h-1],[w-1,h-1],[w-1,0] ]).reshape(-1,1,2)
					dst = cv2.perspectiveTransform(pts,M)

					img2 = cv2.polylines(img2,[np.int32(dst)],True,255,3, cv2.LINE_AA)
				
					# Paramètre des traits des résultats qu'on a trouvé
					draw_params = dict(matchColor = (0,255,0), # draw matches in green color
									   singlePointColor = None,
									   matchesMask = matchesMask, # draw only inliers
									   flags = 2)

					# Dessine les traits sur l'image résultat
					img3 = cv2.drawMatches(img1,kp1,img2,kp2,good,None,**draw_params)
					title = logo
				else :
					print("Il y a eu une erreur", end="\n\n")

			else:
				print("Pas assez de points trouvés - %d/%d" % (len(good),MIN_MATCH_COUNT))
				matchesMask = None
				
		decorator("Fin")
			
	return img3,title


if __name__ == "__main__":
	
	if len(sys.argv) < 2 :
		print("Entrer un nombre valide d'argument")
	else :
		imgToFind = sys.argv[1]
		if isfile(imgToFind) is True :
			img3, logo = findLogoMatch(imgToFind)
			title = logo[:-9].title()

			if img3 is not None:
				plt.title(title)
				plt.imshow(img3, 'gray'),plt.show()
		else :
			print("Entrez un fichier valide")